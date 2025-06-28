package com.fleencorp.feen.poll.mapper.impl;

import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.poll.mapper.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.holder.PollOptionEntriesHolder;
import com.fleencorp.feen.poll.model.holder.PollResponseEntriesHolder;
import com.fleencorp.feen.poll.model.info.*;
import com.fleencorp.feen.poll.model.response.base.PollOptionResponse;
import com.fleencorp.feen.poll.model.response.base.PollResponse;
import com.fleencorp.feen.poll.model.response.base.PollStatResponse;
import com.fleencorp.feen.poll.model.response.base.PollVoteResponse;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserResponse;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.nonNull;

@Component
public final class PollMapperImpl implements PollMapper {

  private static final List<String> COLOR_PALETTE = List.of(
    "#FF5733", "#33FF57", "#3357FF", "#F1C40F", "#9B59B6", "#1ABC9C", "#E74C3C", "#34495E", "#2ECC71", "#E67E22"
  );

  private final ToInfoMapper toInfoMapper;

  public PollMapperImpl(final ToInfoMapper toInfoMapper) {
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Converts a {@link Poll} entity into a {@link PollResponse} DTO.
   *
   * <p>This method maps all relevant poll details including metadata, voting status, author information,
   * and associated poll options. If the poll is {@code null}, the method returns {@code null}.</p>
   *
   * @param entry the {@link Poll} entity to convert
   * @return a {@link PollResponse} representing the poll, or {@code null} if the input is null
   */
  @Override
  public PollResponse toPollResponse(final Poll entry) {
    if (nonNull(entry)) {
      final PollResponse response = new PollResponse();
      response.setId(entry.getPollId());
      response.setQuestion(entry.getQuestion());
      response.setDescription(entry.getDescription());

      response.setExpiresAt(entry.getExpiresAt());
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final PollVisibilityInfo pollVisibilityInfo = toInfoMapper.toPollVisibilityInfo(entry.getVisibility());
      response.setPollVisibilityInfo(pollVisibilityInfo);

      final IsAnonymousInfo isAnonymousInfo = toInfoMapper.toIsAnonymousInfo(entry.isAnonymous());
      response.setIsAnonymousInfo(isAnonymousInfo);

      final IsMultipleChoiceInfo isMultipleChoiceInfo = toInfoMapper.toIsMultipleChoiceInfo(entry.isMultipleChoice());
      response.setIsMultipleChoiceInfo(isMultipleChoiceInfo);

      final IsEndedInfo isEndedInfo = toInfoMapper.toIsEnded(entry.hasEnded());
      response.setIsEndedInfo(isEndedInfo);

      final IsVotedInfo isVotedInfo = toInfoMapper.toIsVotedInfo(false);

      final Collection<PollOption> pollOptions = entry.getOptions();
      final PollOptionEntriesHolder pollOptionEntriesHolder = PollOptionEntriesHolder.from(pollOptions);
      final Collection<PollOptionResponse> options = toPollOptionResponses(pollOptions, pollOptionEntriesHolder, new ArrayList<>());
      response.setPollOptions(options);

      final TotalPollVoteEntriesInfo totalPollVoteEntriesInfo = toInfoMapper.toTotalPollVoteEntriesInfo(pollOptionEntriesHolder.totalVotes());
      response.setTotalPollVoteEntriesInfo(totalPollVoteEntriesInfo);

      final PollVoteResponse pollVoteResponse = PollVoteResponse.of(isVotedInfo, totalPollVoteEntriesInfo);
      response.setPollVote(pollVoteResponse);

      final Member author = entry.getAuthor();
      final UserResponse userResponse = UserResponse.of(
        author.getUsername(),
        author.getFullName(),
        author.getProfilePhotoUrl()
      );
      response.setAuthor(userResponse);

      return response;
    }

    return null;
  }

  /**
   * Converts a list of {@link Poll} entities into a {@link PollResponseEntriesHolder} containing {@link PollResponse} DTOs.
   *
   * <p>If the input list is {@code null} or empty, an empty holder is returned. Null elements within
   * the list are ignored during conversion.</p>
   *
   * @param entries the list of {@link Poll} entities to convert
   * @return a {@link PollResponseEntriesHolder} containing the converted poll responses
   */
  @Override
  public PollResponseEntriesHolder toPollResponses(final List<Poll> entries) {
    Collection<PollResponse> pollResponses = new ArrayList<>();
    if (nonNull(entries) && !entries.isEmpty()) {
      pollResponses = entries.stream()
        .filter(Objects::nonNull)
        .map(this::toPollResponse)
        .toList();
    }

    return PollResponseEntriesHolder.of(pollResponses);
  }

  /**
   * Converts a {@link PollOption} entity into a {@link PollOptionResponse} DTO.
   *
   * <p>The resulting response contains the option's ID, text, and timestamps for creation and last update.</p>
   *
   * @param option the {@link PollOption} to convert
   * @return the corresponding {@link PollOptionResponse}
   */
  @Override
  public PollOptionResponse toPollOptionResponse(final PollOption option) {
    final PollOptionResponse response = new PollOptionResponse();
    response.setId(option.getPollOptionId());
    response.setOptionText(option.getOptionText());
    response.setCreatedOn(option.getCreatedOn());
    response.setUpdatedOn(option.getUpdatedOn());

    return response;
  }

  /**
   * Converts a collection of {@link PollOption} entities into a collection of {@link PollOptionResponse} DTOs.
   *
   * <p>Returns an empty list if the input is {@code null} or empty. Null elements in the collection
   * are filtered out before conversion. Each valid poll option is mapped to a {@link PollOptionResponse}.</p>
   *
   * @param entries the collection of poll options
   * @return a collection of {@link PollOptionResponse} objects
   */
  @Override
  public Collection<PollOptionResponse> toVotedPollOptionResponses(final Collection<PollOption> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toPollOptionResponse)
        .peek(PollOptionResponse::markUserVoted)
        .toList();
    }

    return List.of();
  }

  /**
   * Converts a collection of {@link PollOption} entities into a collection of {@link PollOptionResponse} DTOs,
   * enriching them with vote counts and computed statistics.
   *
   * <p>If the input is {@code null}, an empty list is returned. Each valid poll option is mapped to a
   * {@link PollOptionResponse} that includes its vote count and a {@link PollStatResponse} showing the
   * percentage of total votes and a color. If no votes have been cast, all options receive a default
   * color and 0%.</p>
   *
   * @param entries the collection of poll options
   * @param pollOptionEntriesHolder holds vote-related data for the poll options
   * @return a collection of {@link PollOptionResponse} objects containing vote statistics
   */
  @Override
  public Collection<PollOptionResponse> toPollOptionResponses(final Collection<PollOption> entries, final PollOptionEntriesHolder pollOptionEntriesHolder, final Collection<Long> votedPollOptionIds) {

    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(option -> buildPollOptionResponse(pollOptionEntriesHolder, votedPollOptionIds, option))
      .toList();
  }

  /**
   * Builds a {@link PollOptionResponse} DTO from a given {@link PollOption} entity, enriching it with
   * vote count, user voting status, and computed statistics such as percentage of total votes and a color.
   *
   * <p>This method uses the vote data from the {@link PollOptionEntriesHolder} and checks if the user
   * has voted for the given option using the {@code votedPollOptionIds}. If no votes have been cast at all,
   * the statistics default to 0% with a predefined color.</p>
   *
   * @param pollOptionEntriesHolder holds vote-related data such as total votes and individual counts per option
   * @param votedPollOptionIds the set of poll option IDs the current user has voted for
   * @param option the poll option entity to convert
   * @return a fully populated {@link PollOptionResponse} including vote count, statistics, and user vote status
   */
  private PollOptionResponse buildPollOptionResponse(final PollOptionEntriesHolder pollOptionEntriesHolder, final Collection<Long> votedPollOptionIds, final PollOption option) {
    final boolean isZeroTotal = pollOptionEntriesHolder.isZeroTotalVotes();
    final int totalVotes = pollOptionEntriesHolder.totalVotes();
    final String defaultColor = "#BDC3C7";

    final Long optionId = option.getPollOptionId();
    final Integer pollOptionTotalEntries = pollOptionEntriesHolder.pollOptionTotalEntries(optionId);
    final Boolean isOptionVoted = votedPollOptionIds.contains(optionId);

    final PollStatResponse stat = isZeroTotal
      ? PollStatResponse.of(defaultColor, 0.0)
      : buildPollStat(pollOptionTotalEntries, totalVotes, optionId);

    final PollOptionResponse response = toPollOptionResponse(option);
    response.setVoteCount(pollOptionTotalEntries);
    response.setStat(stat);
    response.setUserVoted(isOptionVoted);

    return response;
  }

  /**
   * Converts a collection of {@link Member} objects into a collection of {@link UserResponse} DTOs.
   *
   * <p>If the input collection is {@code null}, an empty list is returned. Null members within the
   * collection are filtered out. Each valid member is mapped to a {@link UserResponse} using
   * their username, full name, and profile photo URL.</p>
   *
   * @param entries the collection of members who voted in the poll
   * @return a collection of {@link UserResponse} objects representing the poll voters
   */
  @Override
  public Collection<UserResponse> toPollVoteResponses(final Collection<Member> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(member -> UserResponse.of(
        member.getUsername(),
        member.getFullName(),
        member.getProfilePhotoUrl()
      ))
      .toList();
  }

  /**
   * Maps a boolean vote status to an {@link IsVotedInfo} representation.
   *
   * <p>Delegates to {@code toInfoMapper} to convert the given boolean indicating whether
   * a poll has been voted on into an {@link IsVotedInfo} object.</p>
   *
   * @param isVoted {@code true} if the user has voted on the poll, {@code false} otherwise
   * @return an {@link IsVotedInfo} representing the vote status
   */
  @Override
  public IsVotedInfo toIsVotedInfo(final boolean isVoted) {
    return toInfoMapper.toIsVotedInfo(isVoted);
  }

  /**
   * Delegates the creation of a {@link TotalPollVoteEntriesInfo} object to {@code toInfoMapper}.
   *
   * <p>This method simply forwards the total vote count to the {@code toInfoMapper.toTotalPollVoteEntriesInfo}
   * method, which is responsible for translating and constructing the response DTO.</p>
   *
   * @param pollVoteEntries the total number of votes recorded in the poll
   * @return a {@link TotalPollVoteEntriesInfo} with the vote count and its localized messages
   */
  @Override
  public TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(final Integer pollVoteEntries) {
    return toInfoMapper.toTotalPollVoteEntriesInfo(pollVoteEntries);
  }

  /**
   * Builds a {@link PollStatResponse} representing statistics for a poll option.
   *
   * <p>Calculates the percentage of votes this option received out of the total votes,
   * rounds it to two decimal places, and assigns a consistent color using the option ID.</p>
   *
   * @param entries the number of votes for the option
   * @param totalVotes the total number of votes cast in the poll
   * @param optionId the ID of the poll option
   * @return a {@link PollStatResponse} containing the percentage and color for the option
   */
  private static PollStatResponse buildPollStat(final int entries, final int totalVotes, final Long optionId) {
    final String color = colorForOption(optionId);

    final double percentage = totalVotes == 0 ? 0.0 : ((double) entries / totalVotes) * 100;
    final double roundedPercentage = Math.round(percentage * 100.0) / 100.0;
    return new PollStatResponse(color, roundedPercentage);
  }

  /**
   * Determines a consistent color for the given poll option ID using a predefined color palette.
   *
   * <p>The method hashes the option ID, ensures it's a non-negative value, and selects a color from
   * {@code COLOR_PALETTE} based on the hash result modulo the palette size.</p>
   *
   * @param optionId the ID of the poll option
   * @return a color string selected from the color palette
   */
  private static String colorForOption(final Long optionId) {
    final int index = (int) (optionId % COLOR_PALETTE.size());
    return COLOR_PALETTE.get(index);
  }

}

