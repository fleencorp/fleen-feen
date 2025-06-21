package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.mapper.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.domain.PollVote;
import com.fleencorp.feen.poll.model.form.PollFormField;
import com.fleencorp.feen.poll.model.form.field.PollFormFieldGuide;
import com.fleencorp.feen.poll.model.holder.PollResponseEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollVoteEntriesHolder;
import com.fleencorp.feen.poll.model.info.IsVotedInfo;
import com.fleencorp.feen.poll.model.info.PollVisibilityInfo;
import com.fleencorp.feen.poll.model.request.PollSearchRequest;
import com.fleencorp.feen.poll.model.response.GetDataRequiredToCreatePoll;
import com.fleencorp.feen.poll.model.response.PollRetrieveResponse;
import com.fleencorp.feen.poll.model.response.base.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.base.PollResponse;
import com.fleencorp.feen.poll.model.response.base.PollVoteResponse;
import com.fleencorp.feen.poll.model.search.ChatSpacePollSearchResult;
import com.fleencorp.feen.poll.model.search.PollSearchResult;
import com.fleencorp.feen.poll.model.search.StreamPollSearchResult;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import com.fleencorp.localizer.service.adapter.DefaultLocalizer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PollSearchServiceImpl implements PollSearchService {

  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollMapper pollMapper;
  private final DefaultLocalizer defaultLocalizer;
  private final Localizer localizer;

  public PollSearchServiceImpl(
      final PollCommonService pollCommonService,
      final PollOperationsService pollOperationsService,
      final PollMapper pollMapper,
      final DefaultLocalizer defaultLocalizer,
      final Localizer localizer) {
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.pollMapper = pollMapper;
    this.defaultLocalizer = defaultLocalizer;
    this.localizer = localizer;
  }

  /**
   * Retrieves the data required to display or initialize the poll creation process.
   *
   * <p>This method builds a map of all possible {@link PollVisibility} values to their corresponding
   * {@link PollVisibilityInfo} representations, which include localized display and description messages.
   * The resulting map is used to create a {@link GetDataRequiredToCreatePoll} object that holds the
   * poll creation metadata.</p>
   *
   * <p>The returned object is then localized using the {@code localizer} to ensure all translatable
   * fields are resolved according to the current locale.</p>
   *
   * @return a {@link GetDataRequiredToCreatePoll} object containing localized information about poll visibilities
   */
  @Override
  @Cacheable("dataToCreatePoll")
  public GetDataRequiredToCreatePoll getDataRequiredToCreatePoll() {
    final Map<PollVisibility, PollVisibilityInfo> availablePollVisibilities =
      Stream.of(PollVisibility.values())
        .collect(Collectors.collectingAndThen(
          Collectors.toMap(
            pv -> pv,
            pv -> PollVisibilityInfo.of(
              pv,
              defaultLocalizer.getMessage(pv.getLabelCode()),
              defaultLocalizer.getMessage(pv.getMessageCode())
            ),
            (_, b) -> b,
            () -> new EnumMap<>(PollVisibility.class)
          ),
          Map::copyOf
        ));

    final Map<PollFormField, PollFormFieldGuide> formFieldsGuide =
      Stream.of(PollFormField.values())
        .collect(Collectors.collectingAndThen(
          Collectors.toMap(
            guide -> guide,
            guide -> PollFormFieldGuide.of(
              guide,
              defaultLocalizer.getMessage(guide.getDescription())
            ),
            (_, b) -> b,
            () -> new EnumMap<>(PollFormField.class)
          ),
          Map::copyOf
        ));

    final GetDataRequiredToCreatePoll getDataRequiredToCreatePoll = GetDataRequiredToCreatePoll.of(availablePollVisibilities, formFieldsGuide);
    return localizer.of(getDataRequiredToCreatePoll);
  }

  /**
   * Retrieves a poll by its ID and returns a localized {@link PollRetrieveResponse}.
   *
   * <p>This method fetches the poll entity using the provided {@code pollId}. It then maps the entity
   * to a {@link PollResponse} and wraps it into a {@link PollRetrieveResponse} object. The response
   * is localized before being returned.</p>
   *
   * @param pollId the ID of the poll to retrieve
   * @return a localized {@link PollRetrieveResponse} containing the poll details
   * @throws PollNotFoundException if no poll exists with the given ID
   */
  @Override
  @Transactional(readOnly = true)
  public PollRetrieveResponse findPoll(final Long pollId) throws PollNotFoundException {
    final Poll poll = pollCommonService.findPollById(pollId);

    final PollResponse pollResponse = pollMapper.toPollResponse(poll);
    final PollRetrieveResponse pollRetrieveResponse = PollRetrieveResponse.of(pollId, pollResponse);

    return localizer.of(pollRetrieveResponse);
  }

  /**
   * Searches for polls based on the given {@link PollSearchRequest} and returns a localized {@link PollSearchResult}.
   *
   * <p>This method delegates the search logic to {@code findPollsWithResult}, converting the {@link RegisteredUser}
   * to a {@link Member} and wrapping the result using {@code PollSearchResult::of}. The final result is then localized
   * before being returned.</p>
   *
   * @param searchRequest the poll search criteria
   * @param user the authenticated user requesting the poll data
   * @return a localized {@link PollSearchResult} containing the matched polls
   */
  @Override
  @Transactional(readOnly = true)
  public PollSearchResult findPolls(final PollSearchRequest searchRequest, final RegisteredUser user) {
    final PollSearchResult pollSearchResult = findPollsWithResult(searchRequest, user.toMember(), PollSearchResult::of);
    return localizer.of(pollSearchResult);
  }

  /**
   * Searches for chat space polls based on the given {@link PollSearchRequest} and returns a localized {@link ChatSpacePollSearchResult}.
   *
   * <p>This method delegates the search operation to {@code findPollsWithResult}, converting the {@link RegisteredUser}
   * to a {@link Member} and wrapping the result using {@code ChatSpacePollSearchResult::of}. The resulting response is then
   * localized and returned.</p>
   *
   * @param searchRequest the poll search criteria specific to chat spaces
   * @param user the authenticated user requesting the chat space polls
   * @return a localized {@link ChatSpacePollSearchResult} containing the matched polls
   */
  @Override
  @Transactional(readOnly = true)
  public ChatSpacePollSearchResult findChatSpacePolls(final PollSearchRequest searchRequest, final RegisteredUser user) {
    final ChatSpacePollSearchResult chatSpacePollSearchResult = findPollsWithResult(searchRequest, user.toMember(), ChatSpacePollSearchResult::of);
    return localizer.of(chatSpacePollSearchResult);
  }

  /**
   * Searches for stream polls based on the given {@link PollSearchRequest} and returns a localized {@link StreamPollSearchResult}.
   *
   * <p>This method delegates the search to {@code findPollsWithResult}, using the {@link RegisteredUser}'s member representation
   * and wrapping the result with {@code StreamPollSearchResult::of}. The final response is localized before being returned.</p>
   *
   * @param searchRequest the poll search criteria specific to streams
   * @param user the authenticated user requesting the stream polls
   * @return a localized {@link StreamPollSearchResult} containing the matched stream polls
   */
  @Override
  @Transactional(readOnly = true)
  public StreamPollSearchResult findStreamPolls(final PollSearchRequest searchRequest, final RegisteredUser user) {
    final StreamPollSearchResult streamPollSearchResult = findPollsWithResult(searchRequest, user.toMember(), StreamPollSearchResult::of);
    return localizer.of(streamPollSearchResult);
  }

  /**
   * Searches for polls created by the given {@link RegisteredUser} and returns a localized {@link PollSearchResult}.
   *
   * <p>This method delegates the search to {@code findPollsWithResult}, converting the user to a {@link Member}
   * and wrapping the result with {@code PollSearchResult::of}. The resulting response is localized before being returned.</p>
   *
   * @param searchRequest the poll search criteria for retrieving the user's own polls
   * @param user the authenticated user whose polls are to be retrieved
   * @return a localized {@link PollSearchResult} containing the user's polls
   */
  @Override
  @Transactional(readOnly = true)
  public PollSearchResult findMyPolls(final PollSearchRequest searchRequest, final RegisteredUser user) {
    searchRequest.setAuthor(user.toMember());
    final PollSearchResult pollSearchResult = findPolls(searchRequest, user);
    return localizer.of(pollSearchResult);
  }

  /**
   * Finds polls based on the given {@link PollSearchRequest}, maps them to responses, and wraps the result using the provided function.
   *
   * <p>This method retrieves a paginated list of polls using {@code findPolls}, maps them to {@link PollResponse} objects,
   * and constructs a {@link SearchResult} instance. It then enriches the poll responses with additional member-specific details
   * via {@code processPollOtherDetails}. Finally, it applies the {@code resultWrapper} function to the {@code SearchResult}
   * and returns the result.</p>
   *
   * @param searchRequest the request containing poll search criteria
   * @param member the member whose context is used for additional poll processing
   * @param resultWrapper a function to transform the {@code SearchResult} into a result of type {@code T}
   * @return the result of applying the {@code resultWrapper} to the constructed {@code SearchResult}
   */
  protected  <T> T findPollsWithResult(final PollSearchRequest searchRequest, final Member member, final Function<SearchResult, T> resultWrapper) {
    final Page<Poll> page = findPolls(searchRequest);
    final PollResponseEntriesHolder pollResponseEntriesHolder = pollMapper.toPollResponses(page.getContent());
    final Collection<PollResponse> pollResponses = pollResponseEntriesHolder.pollResponses();
    final SearchResult searchResult = toSearchResult(pollResponses, page);

    processPollOtherDetails(pollResponseEntriesHolder, member);
    return resultWrapper.apply(searchResult);
  }

  /**
   * Finds polls based on the type of search specified in the {@link PollSearchRequest}.
   *
   * <p>This method evaluates the type of poll search requested and delegates the call to the appropriate
   * method in {@link PollOperationsService}. If the request is for a chat space poll, it calls {@code findByChatSpace}.
   * If it's for a stream poll, it uses {@code findByStream}. If the request is filtered by author, it calls {@code findByAuthor}.
   * If none of these specific filters are present, it defaults to {@code findMany} to retrieve a general list of polls.</p>
   *
   * @param searchRequest the search request containing filters and pagination details
   * @return a page of {@link Poll} entities matching the criteria in the search request
   */
  protected Page<Poll> findPolls(final PollSearchRequest searchRequest) {
    final Pageable pageable = searchRequest.getPage();

    if (searchRequest.isChatSpacePollSearchRequest()) {
      return pollOperationsService.findByChatSpace(searchRequest.getParentId(), pageable);
    } else if (searchRequest.isStreamPollSearchRequest()) {
      return pollOperationsService.findByStream(searchRequest.getParentId(), pageable);
    } else if (searchRequest.isByAuthor()) {
      return pollOperationsService.findByAuthor(searchRequest.getAuthorId(), pageable);
    }

    return pollOperationsService.findMany(pageable);
  }

  /**
   * Enriches each {@link PollResponse} with the voting details of the specified {@link Member}, if applicable.
   *
   * <p>If the member is {@code null} or the {@code pollResponseEntriesHolder} does not contain any polls,
   * the method returns immediately. Otherwise, it retrieves all poll IDs and fetches the corresponding
   * {@link PollVote} entries made by the member. It then updates each {@link PollResponse} with the user's vote,
   * using {@code setUserVote}.</p>
   *
   * @param pollResponseEntriesHolder the holder containing the poll responses to process
   * @param member the member whose voting details should be applied
   */
  protected void processPollOtherDetails(final PollResponseEntriesHolder pollResponseEntriesHolder, final Member member) {
    if (isNull(member) || pollResponseEntriesHolder.hasPolls()) {
      return;
    }

    final Collection<Long> pollIds = pollResponseEntriesHolder.getPollIds();
    if (pollIds.isEmpty()) {
      return;
    }

    final Collection<PollResponse> pollResponses = pollResponseEntriesHolder.pollResponses();
    final PollVoteEntriesHolder pollVoteEntriesHolder = pollOperationsService.findVotesByPollIdsAndMemberId(pollIds, member.getMemberId());

    pollResponses.stream()
      .filter(Objects::nonNull)
      .forEach(pollResponse -> setUserVote(pollResponse, pollVoteEntriesHolder));
  }

  /**
   * Sets the user's vote information on the given {@link PollResponse}, if available.
   *
   * <p>This method retrieves the list of {@link PollVote} entries for the poll using its ID from the provided
   * {@link PollVoteEntriesHolder}. If votes are found, it delegates to {@code setPollVoteOptions}
   * to populate the vote details in the response.</p>
   *
   * @param pollResponse the poll response to update with the user's vote
   * @param pollVoteEntriesHolder the holder containing vote entries mapped by poll ID
   */
  protected void setUserVote(final PollResponse pollResponse, final PollVoteEntriesHolder pollVoteEntriesHolder) {
    final Long pollId = pollResponse.getNumberId();
    final List<PollVote> votes = pollVoteEntriesHolder.getPollVotes(pollId);

    if (nonNull(votes) && !votes.isEmpty()) {
      setPollVoteOptions(pollResponse, votes);
    }
  }

  /**
   * Sets the poll vote options and voting status on the given {@link PollResponse}.
   *
   * <p>This method extracts the {@link PollOption} objects from the provided list of {@link PollVote} entities,
   * maps them to {@link PollOptionResponse} objects, and determines whether any votes were cast.
   * It then builds a {@link PollVoteResponse} containing the option responses and voting status,
   * and sets it on the provided {@code pollResponse}.</p>
   *
   * @param pollResponse the response object to enrich with vote information
   * @param votes the list of votes associated with the poll
   */
  protected void setPollVoteOptions(final PollResponse pollResponse, final List<PollVote> votes) {
    final List<PollOption> pollOptions = votes.stream()
      .map(PollVote::getPollOption)
      .toList();

    final Collection<PollOptionResponse> optionResponses = pollMapper.toPollOptionResponses(pollOptions);

    final boolean isVoted = !votes.isEmpty();
    final IsVotedInfo isVotedInfo = pollMapper.toIsVotedInfo(isVoted);

    final PollVoteResponse pollVoteResponse = PollVoteResponse.of(optionResponses, isVotedInfo);
    pollResponse.setPollVote(pollVoteResponse);
  }

}
