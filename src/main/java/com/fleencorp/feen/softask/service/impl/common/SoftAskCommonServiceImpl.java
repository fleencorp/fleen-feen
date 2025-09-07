package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.model.contract.UserHaveOtherDetail;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.mapper.UserLocationMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.common.UpdateSoftAskContentDto;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.response.common.SoftAskContentUpdateResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.service.vote.SoftAskVoteSearchService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class SoftAskCommonServiceImpl implements SoftAskCommonService {

  private final BookmarkOperationService bookmarkOperationService;
  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskOperationService softAskOperationService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskVoteSearchService softAskVoteSearchService;
  private final UserLocationMapper userLocationMapper;
  private final Localizer localizer;

  public SoftAskCommonServiceImpl(
      final BookmarkOperationService bookmarkOperationService,
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskOperationService softAskOperationService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskVoteSearchService softAskVoteSearchService,
      final UserLocationMapper userLocationMapper,
      final Localizer localizer) {
    this.bookmarkOperationService = bookmarkOperationService;
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskOperationService = softAskOperationService;
    this.softAskSearchService = softAskSearchService;
    this.softAskVoteSearchService = softAskVoteSearchService;
    this.userLocationMapper = userLocationMapper;
    this.localizer = localizer;
  }

  @Transactional(readOnly = true)
  protected <T extends SoftAskCommonResponse> void processResponsesInternal(
    Collection<T> softAskCommonResponses,
    IsAMember member,
    UserHaveOtherDetail userHaveOtherDetail) {

    softAskCommonResponses.forEach(commonResponse -> {
      userLocationMapper.setLocationDetails(commonResponse, userHaveOtherDetail);

      if (nonNull(member)) {
        setEntityUpdatableByUser((Updatable) commonResponse, member.getMemberId());
      }
    });
  }

  @Override
  @Transactional(readOnly = true)
  public <T extends SoftAskCommonResponse> void processSoftAskResponses(Collection<T> softAskCommonResponses, IsAMember member, UserHaveOtherDetail userHaveOtherDetail) {

    if (nonNull(softAskCommonResponses)) {
      processBookmarkForResponses(softAskCommonResponses, member);
      softAskVoteSearchService.processVotesForResponses(softAskCommonResponses, member);
      processResponsesInternal(softAskCommonResponses, member, userHaveOtherDetail);
    }
  }

  /**
   * Processes bookmark information for a collection of soft ask responses.
   *
   * <p>The method checks if the given responses are non-null and inspects the type
   * of the first response in the collection. If the response type is a soft ask,
   * it delegates to {@link BookmarkOperationService#populateSoftAskBookmarksFor(Collection, IsAMember)}.
   * Otherwise, it delegates to
   * {@link BookmarkOperationService#populateSoftAskReplyBookmarksFor(Collection, IsAMember)}.</p>
   *
   * @param responses the collection of responses to process
   * @param member the member for whom the bookmarks are being populated
   */
  private <T extends SoftAskCommonResponse> void processBookmarkForResponses(final Collection<T> responses, final IsAMember member) {
    if (nonNull(responses) && nonNull(member)) {
      final Optional<T> anyResponse = responses.stream().findFirst();

      anyResponse.ifPresent(response -> {
        if (SoftAskType.isSoftAsk(response.getSoftAskType())) {
          bookmarkOperationService.populateSoftAskBookmarksFor(responses, member);
        } else if (SoftAskType.isReply(response.getSoftAskType())) {
          bookmarkOperationService.populateSoftAskReplyBookmarksFor(responses, member);
        }
      });
    }
  }

  /**
   * Finds a limited number of replies for a given soft ask and enriches them with their child replies.
   *
   * <p>This method updates the provided {@link SoftAskSearchRequest} with the parent ID from
   * the given {@link SoftAskResponse} and sets a fixed page size of 10. It then retrieves
   * the replies for the soft ask by delegating to the {@code softAskReplySearchService}.
   * For each reply found, the method further retrieves a limited number of its child replies
   * by calling {@code findSomeSoftAskChildReplyForReply}, and attaches the result to the
   * corresponding {@link SoftAskReplyResponse}.</p>
   *
   * @param searchRequest   the search request to configure and use for finding replies
   * @param softAskResponse the parent soft ask whose replies should be retrieved
   * @param member          the member performing the search
   * @return a {@link SoftAskReplySearchResult} containing the replies and their associated child replies
   */
  @Override
  @Transactional(readOnly = true)
  public SoftAskReplySearchResult findSomeSoftAskRepliesForSoftAsk(final SoftAskSearchRequest searchRequest, final SoftAskResponse softAskResponse, final IsAMember member) {
    searchRequest.updateParentId(softAskResponse.getNumberId());
    log.info("The parent ID is {}", searchRequest.getParentId());
    searchRequest.setPageSize(10);

    final SoftAskReplySearchResult softAskReplySearchResult = softAskReplySearchService.findSoftAskReplies(searchRequest, member);
    log.info("The search result is {}", softAskReplySearchResult.getResult().getTotalEntries());
    log.info("The search result is {}", softAskReplySearchResult.getResult().getTotalEntries());
    final SearchResult<SoftAskReplyResponse> searchResult = softAskReplySearchResult.getResult();

    searchResult.getValues().forEach(softAskReplyResponse -> {
      final SoftAskReplySearchResult softAskChildReplySearchResult = findSomeSoftAskChildReplyForReply(softAskReplyResponse, member);
      softAskReplyResponse.setChildRepliesSearchResult(softAskChildReplySearchResult);
    });

    return softAskReplySearchResult;
  }

  /**
   * Finds some child replies of a given soft ask reply.
   *
   * <p>Creates a search request using the parent ID and number ID from the provided
   * {@code softAskReplyResponse}, limits the result to 10 items, and retrieves matching
   * soft ask replies for the given {@code member}.</p>
   *
   * @param softAskReplyResponse the soft ask reply response containing parent and number IDs
   * @param member the member performing the search
   * @return a search result containing soft ask replies matching the criteria
   */
  private SoftAskReplySearchResult findSomeSoftAskChildReplyForReply(final SoftAskReplyResponse softAskReplyResponse, final IsAMember member) {
    log.info("Somebody is trying to get here");
    final SoftAskSearchRequest searchRequest = SoftAskSearchRequest.of(softAskReplyResponse.getParentId(), softAskReplyResponse.getNumberId());
    searchRequest.setPageSize(10);

    log.info("Somebody got here");
    log.info("The parent id is {} and the reply id is {}", softAskReplyResponse.getParentId(), softAskReplyResponse.getNumberId());
    return softAskReplySearchService.findSoftAskReplies(searchRequest, member);
  }

  /**
   * Updates the content of a soft ask or soft ask reply.
   *
   * <p>The method finds the soft ask entity or reply based on the IDs and type provided
   * in {@code updateSoftAskContentDto}. It verifies that the {@code user} is the author,
   * updates the content, saves the changes, and returns a localized update response.</p>
   *
   * @param updateSoftAskContentDto the data transfer object containing update details
   * @param user the currently authenticated user performing the update
   * @return a localized response confirming the content update
   * @throws SoftAskReplyNotFoundException if the soft ask reply is not found
   * @throws SoftAskUpdateDeniedException if the user is not authorized to update the content
   * @throws FailedOperationException if the update operation fails for any other reason
   */
  @Override
  @Transactional
  public SoftAskContentUpdateResponse updateSoftAskContent(final UpdateSoftAskContentDto updateSoftAskContentDto, final RegisteredUser user)
    throws SoftAskNotFoundException, SoftAskReplyNotFoundException, SoftAskUpdateDeniedException,
      FailedOperationException {

    final Long softAskId = updateSoftAskContentDto.getSoftAskId();
    final Long softAskReplyId = updateSoftAskContentDto.getSoftAskReplyId();
    final SoftAskCommonData softAskCommonData = findSoftAskTypeToUpdate(softAskId, softAskReplyId, updateSoftAskContentDto.getSoftAskType());

    softAskCommonData.checkIsAuthor(user.getId());
    softAskCommonData.setContent(updateSoftAskContentDto.getContent());
    saveSoftAskCommonData(softAskCommonData);

    final SoftAskContentUpdateResponse softAskContentUpdateResponse = SoftAskContentUpdateResponse.of(softAskId, softAskReplyId);
    return localizer.of(softAskContentUpdateResponse);
  }

  /**
   * Finds and returns the soft ask entity to update based on the given type.
   *
   * <p>If the {@code softAskType} is {@code REPLY}, it retrieves the corresponding soft ask reply
   * using {@code softAskReplySearchService}. If the type is {@code SOFT_ASK}, it retrieves the
   * soft ask using {@code softAskSearchService} and performs a validation to ensure there is not
   * more than one reply.</p>
   *
   * @param softAskId the ID of the soft ask
   * @param softAskReplyId the ID of the soft ask reply (used if {@code softAskType} is {@code REPLY})
   * @param softAskType the type of soft ask to find, either {@code REPLY} or {@code SOFT_ASK}
   * @return the soft ask or soft ask reply entity to update
   * @throws FailedOperationException if {@code softAskType} is null or not recognized
   */
  private SoftAskCommonData findSoftAskTypeToUpdate(final Long softAskId, final Long softAskReplyId, final SoftAskType softAskType) {
    if (nonNull(softAskType)) {

      return switch (softAskType) {
        case SOFT_ASK_REPLY -> softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId);
        case SOFT_ASK -> {
          final SoftAsk softAsk = softAskSearchService.findSoftAsk(softAskId);
          softAsk.checkIsReplyIsNotMoreThanOne();
          yield softAsk;
        }
      };
    }

    throw FailedOperationException.of();
  }

  /**
   * Persists a {@link SoftAskCommonData} entity using the {@link SoftAskOperationService},
   * based on its concrete type.
   *
   * <p>If the entity is a {@link SoftAskReply}, or {@link SoftAsk},
   * it is saved via the corresponding overload of {@code softAskOperationService.save(...)}.</p>
   *
   * @param softAskCommonData the entity to persist; must be an instance of {@link SoftAsk}, or {@link SoftAskReply}.
   */
  private void saveSoftAskCommonData(final SoftAskCommonData softAskCommonData) {
    if (softAskCommonData instanceof final SoftAskReply softAskReply) {
      softAskOperationService.save(softAskReply);
    } else if (softAskCommonData instanceof final SoftAsk softAsk) {
      softAskOperationService.save(softAsk);
    }
  }

}
