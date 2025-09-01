package com.fleencorp.feen.poll.mapper.impl.common;

import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.poll.constant.*;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.mapper.common.PollInfoMapper;
import com.fleencorp.feen.poll.model.info.*;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class PollInfoMapperImpl extends BaseMapper implements PollInfoMapper {

  public PollInfoMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Converts a boolean indicating poll completion status into a localized {@link PollIsEndedInfo} DTO.
   *
   * <p>The method determines the appropriate {@link PollIsEnded} enum based on the boolean value,
   * translates its associated message codes, and returns the result.</p>
   *
   * @param ended true if the poll has ended, false otherwise
   * @return a {@link PollIsEndedInfo} with localized messages for the poll end state
   */
  @Override
  public PollIsEndedInfo toIsEnded(final boolean ended) {
    final PollIsEnded pollIsEnded = PollIsEnded.by(ended);

    return PollIsEndedInfo.of(
      ended,
      translate(pollIsEnded.getMessageCode()),
      translate(pollIsEnded.getMessageCode2())
    );
  }

  /**
   * Converts a boolean indicating whether the poll allows multiple choices into
   * a localized {@link IsPollMultipleChoiceInfo} DTO.
   *
   * <p>The method maps the boolean to an {@link IsPollMultipleChoice} enum and translates
   * both the short and long message codes for that state.</p>
   *
   * @param multipleChoice true if the poll allows multiple choices, false if it is single-choice
   * @return a {@link IsPollMultipleChoiceInfo} containing the boolean value and its translations
   */
  @Override
  public IsPollMultipleChoiceInfo toIsMultipleChoiceInfo(final boolean multipleChoice) {
    final IsPollMultipleChoice isPollMultipleChoice = IsPollMultipleChoice.by(multipleChoice);

    return IsPollMultipleChoiceInfo.of(
      multipleChoice,
      translate(isPollMultipleChoice.getMessageCode()),
      translate(isPollMultipleChoice.getMessageCode2())
    );
  }

  /**
   * Converts a boolean indicating vote status into a detailed localized {@link IsVotedInfo} DTO.
   *
   * <p>The method maps the boolean to an {@link IsVoted} enum and translates four different
   * message codes associated with the vote state. This allows for richer UI feedback such as
   * subtitles, descriptions, or tooltips.</p>
   *
   * @param voted true if the user has voted, false otherwise
   * @return a {@link IsVotedInfo} with the vote flag and four localized message values
   */
  @Override
  public IsVotedInfo toIsVotedInfo(final boolean voted) {
    final IsVoted isVoted = IsVoted.by(voted);

    return IsVotedInfo.of(voted,
      translate(isVoted.getMessageCode()),
      translate(isVoted.getMessageCode2()),
      translate(isVoted.getMessageCode3()),
      translate(isVoted.getMessageCode4())
    );
  }

  /**
   * Wraps the total number of poll vote entries into a localized {@link TotalPollVoteEntriesInfo} DTO.
   *
   * <p>This method attaches both short and descriptive translations to the raw vote count
   * using message codes from the {@link TotalVoteEntries} enum.</p>
   *
   * @param pollVoteEntries the total number of votes cast in the poll
   * @return a {@link TotalPollVoteEntriesInfo} with the vote count and its translations
   */
  @Override
  public TotalPollVoteEntriesInfo toTotalPollVoteEntriesInfo(final Integer pollVoteEntries) {
    final TotalVoteEntries totalVoteEntries = TotalVoteEntries.totalVoteEntries();

    return TotalPollVoteEntriesInfo.of(pollVoteEntries,
      translate(totalVoteEntries.getMessageCode(), pollVoteEntries),
      translate(totalVoteEntries.getMessageCode2(), pollVoteEntries),
      translate(totalVoteEntries.getMessageCode3(), pollVoteEntries)
    );
  }

  /**
   * Converts a boolean indicating anonymity into a localized {@link IsPollAnonymousInfo} DTO.
   *
   * <p>This method uses the {@link IsPollAnonymous} enum to resolve the appropriate message codes,
   * which are then translated and included in the response.</p>
   *
   * @param anonymous true if the poll is anonymous, false otherwise
   * @return a {@link IsPollAnonymousInfo} containing the boolean value and its localized messages
   */
  @Override
  public IsPollAnonymousInfo toIsAnonymousInfo(final boolean anonymous) {
    final IsPollAnonymous isPollAnonymous = IsPollAnonymous.by(anonymous);

    return IsPollAnonymousInfo.of(
      anonymous,
      translate(isPollAnonymous.getMessageCode()),
      translate(isPollAnonymous.getMessageCode2()));
  }


  /**
   * Converts a {@link PollVisibility} enum into a localized {@link PollVisibilityInfo} DTO.
   *
   * <p>The returned object includes the original enum value along with its localized label and message,
   * resolved using message codes associated with the visibility level.</p>
   *
   * @param pollVisibility the visibility enum of the poll
   * @return a {@link PollVisibilityInfo} containing the visibility, label, and message
   */
  @Override
  public PollVisibilityInfo toPollVisibilityInfo(final PollVisibility pollVisibility) {
    return PollVisibilityInfo.of(
      pollVisibility,
      translate(pollVisibility.getLabelCode()),
      translate(pollVisibility.getMessageCode2())
    );
  }

}
