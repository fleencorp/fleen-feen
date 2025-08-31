package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.poll.exception.option.PollUpdateCantChangeOptionsException;
import com.fleencorp.feen.poll.exception.poll.*;
import com.fleencorp.feen.poll.mapper.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.poll.model.dto.UpdatePollDto;
import com.fleencorp.feen.poll.model.holder.PollVoteAggregateHolder;
import com.fleencorp.feen.poll.model.response.PollUpdateResponse;
import com.fleencorp.feen.poll.model.response.core.PollResponse;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollUpdateService;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class PollUpdateServiceImpl implements PollUpdateService {

  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollMapper pollMapper;
  private final Localizer localizer;

  public PollUpdateServiceImpl(
      final PollCommonService pollCommonService,
      final PollOperationsService pollOperationsService,
      final PollMapper pollMapper,
      final Localizer localizer) {
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.pollMapper = pollMapper;
    this.localizer = localizer;
  }

  /**
   * Updates an existing poll with the provided changes from the given DTO.
   *
   * <p>This method performs several validations based on the current poll state, such as checking for vote presence
   * before allowing changes to anonymity, multiple choice configuration, question text, visibility, or options.
   * If any validation fails, an appropriate exception is thrown. If the update is valid, the poll is persisted and
   * a localized response is returned.</p>
   *
   * @param pollId the ID of the poll to update
   * @param updatePollDto the data transfer object containing the updated poll details
   * @param user the authenticated user performing the update
   * @return the updated poll wrapped in a response
   * @throws PollNotFoundException if the poll does not exist
   * @throws PollUpdateUnauthorizedException if the user is not authorized to update the poll
   * @throws PollUpdateCantChangeQuestionException if the question text cannot be changed due to vote constraints
   * @throws PollUpdateCantChangeMultipleChoiceException if multiple choice setting cannot be changed
   * @throws PollUpdateCantChangeVisibilityException if visibility cannot be changed
   * @throws PollUpdateCantChangeAnonymityException if anonymity cannot be changed after votes exist
   * @throws PollUpdateCantChangeOptionsException if poll options cannot be changed due to votes
   * @throws ChatSpaceNotAnAdminException if the user lacks admin privileges in the related chat space
   */
  @Override
  @Transactional
  public PollUpdateResponse updatePoll(final Long pollId, final UpdatePollDto updatePollDto, final RegisteredUser user)
    throws PollNotFoundException, PollUpdateUnauthorizedException, PollUpdateCantChangeQuestionException,
      PollUpdateCantChangeMultipleChoiceException, PollUpdateCantChangeVisibilityException, PollUpdateCantChangeAnonymityException,
      PollUpdateCantChangeOptionsException, ChatSpaceNotAnAdminException {
    final Poll poll = pollCommonService.findPollById(pollId);
    final PollVoteAggregateHolder pollVoteAggregateHolder = pollOperationsService.findPollVoteAggregate(pollId);

    validatePollDetails(poll, updatePollDto, pollVoteAggregateHolder);
    pollCommonService.checkUpdatePermission(poll, user.toMember());

    poll.update(
      updatePollDto.getQuestion(), updatePollDto.getDescription(), updatePollDto.getExpiresAt(),
      updatePollDto.getVisibility(), updatePollDto.isAnonymous(), updatePollDto.isMultipleChoice()
    );

    updatePollOptions(poll, updatePollDto.getPollOptions(), pollVoteAggregateHolder.hasVotes());
    pollOperationsService.save(poll);

    final PollResponse pollResponse = pollMapper.toPollResponse(poll);
    final PollUpdateResponse pollUpdateResponse = PollUpdateResponse.of(pollId, pollResponse);

    return localizer.of(pollUpdateResponse);
  }

  /**
   * Validates that the critical properties of a poll can still be updated, depending on whether votes exist.
   *
   * <p>If the poll already has votes, changes to the question, multiple choice setting, visibility,
   * anonymity, or options are restricted. If any of these properties are attempted to be changed, the method throws
   * the appropriate exception to prevent data integrity issues.</p>
   *
   * @param poll the existing poll to validate
   * @param updatePollDto the incoming update request data
   * @param pollVoteAggregateHolder vote aggregation data indicating whether the poll has existing votes
   * @throws PollUpdateCantChangeQuestionException if the question is being changed after votes exist
   * @throws PollUpdateCantChangeMultipleChoiceException if multiple choice is being changed after votes exist
   * @throws PollUpdateCantChangeVisibilityException if visibility is being changed after votes exist
   * @throws PollUpdateCantChangeAnonymityException if anonymity is being changed after votes exist
   * @throws PollUpdateCantChangeOptionsException if poll options are being changed after votes exist
   */
  protected void validatePollDetails(final Poll poll, final UpdatePollDto updatePollDto, final PollVoteAggregateHolder pollVoteAggregateHolder)
    throws PollUpdateCantChangeQuestionException, PollUpdateCantChangeMultipleChoiceException, PollUpdateCantChangeVisibilityException,
      PollUpdateCantChangeAnonymityException {

    if (pollVoteAggregateHolder.hasVotes()) {
      if (!Objects.equals(poll.getQuestion(), updatePollDto.getQuestion())) {
        throw PollUpdateCantChangeQuestionException.of();
      }
      if (!Objects.equals(poll.isMultipleChoice(), updatePollDto.isMultipleChoice())) {
        throw PollUpdateCantChangeMultipleChoiceException.of();
      }
      if (!Objects.equals(poll.getVisibility(), updatePollDto.getVisibility())) {
        throw PollUpdateCantChangeVisibilityException.of();
      }
      if (poll.isAnonymous() && !Objects.equals(true, updatePollDto.isAnonymous())) {
        throw PollUpdateCantChangeAnonymityException.of();
      }
    }

    // Check option changes
    validateOptionChanges(poll, updatePollDto.getPollOptions(), pollVoteAggregateHolder);
  }

  /**
   * Validates the option changes in a poll update request to ensure they are permissible.
   *
   * <p>This method checks that existing options are not removed or modified when the poll has recorded votes.
   * It also ensures that all new options with IDs correspond to actual existing options. If any validation fails,
   * a {@code PollUpdateCantChangeOptionsException} is thrown.</p>
   *
   * @param poll the poll being updated
   * @param newOptions the updated collection of poll options
   * @param voteAggregateHolder the aggregate vote data for the poll
   * @throws PollUpdateCantChangeOptionsException if any invalid option change is detected
   */
  private void validateOptionChanges(final Poll poll, final Collection<PollOption> newOptions, final PollVoteAggregateHolder voteAggregateHolder) throws PollUpdateCantChangeOptionsException {
    final Collection<Long> existingOptionIds = poll.getPollOptionIds();
    final Collection<Long> newOptionIds = PollOption.getOptionIds(newOptions);
    final boolean hasVotes = voteAggregateHolder.hasVotes();
    final Map<Long, PollOption> existingOptionsById = poll.getOptionsGrouped();

    // Validate existing options
    validateExistingOptions(newOptions, existingOptionIds, existingOptionsById, hasVotes);

    // If votes exist, ensure no existing options were removed
    if (hasVotes && !newOptionIds.containsAll(existingOptionIds)) {
      throw PollUpdateCantChangeOptionsException.of();
    }
  }

  /**
   * Validates that the provided new options do not illegally modify existing poll options.
   *
   * <p>This method ensures that each new option with a non-null ID corresponds to an existing option in the original poll.
   * If an option ID does not exist in the original set, or if its corresponding text is changed while the poll has votes,
   * a {@link PollUpdateCantChangeOptionsException} is thrown. This check helps preserve the integrity of poll data when votes
   * have already been cast.</p>
   *
   * @param newOptions the new or updated PollOption entries provided in the update request
   * @param existingOptionIds a collection of existing option IDs from the original poll
   * @param existingOptionsById a map of existing options keyed by their ID
   * @param hasVotes indicates whether the poll already has votes
   * @throws PollUpdateCantChangeOptionsException if any invalid update is detected
   */
  private void validateExistingOptions(final Collection<PollOption> newOptions, final Collection<Long> existingOptionIds, final Map<Long, PollOption> existingOptionsById, final boolean hasVotes)
    throws PollUpdateCantChangeOptionsException {

    for (final PollOption newOpt : newOptions) {
      final Long newOptionId = newOpt.getPollOptionId();

      if (nonNull(newOptionId)) {
        // Ensure the updated option ID exists in the original poll
        if (!existingOptionIds.contains(newOptionId)) {
          throw PollUpdateCantChangeOptionsException.of();
        }

        // Retrieve the existing option for comparison
        final PollOption existingOpt = Optional.ofNullable(existingOptionsById.get(newOptionId))
          .orElseThrow(PollUpdateCantChangeOptionsException::of);

        // If votes exist, disallow changing the text of an existing option
        if (hasVotes && !Objects.equals(existingOpt.getOptionText(), newOpt.getOptionText())) {
          throw PollUpdateCantChangeOptionsException.of();
        }
      }
    }
  }

  /**
   * Updates the options of a poll based on the provided collection.
   *
   * <p>This method applies changes to the poll's options while enforcing business rules:
   * if the poll already has votes, existing options cannot be modified or removed, and
   * only new options may be added. If no votes exist, options can be freely updated.</p>
   *
   * @param poll       the {@link Poll} entity to update
   * @param options    the new collection of {@link PollOption} to apply to the poll
   * @param hasVotes   {@code true} if the poll already has votes; affects which updates are allowed
   * @throws PollUpdateCantChangeOptionsException if an illegal update is attempted when votes exist
   */
  private void updatePollOptions(final Poll poll, final Collection<PollOption> options, final boolean hasVotes)
    throws PollUpdateCantChangeOptionsException {
    if (options == null) {
      return;
    }

    // Map existing options for efficient lookup
    final Map<Long, PollOption> existingOptionsById = poll.getOptionsGroupedCopy();
    // Track new options to add
    final List<PollOption> optionsToAdd = new ArrayList<>();

    // Process each option in the update request
    for (final PollOption opt : options) {
      final Long optionId = opt.getPollOptionId();
      final String newText = opt.getOptionText();

      if (optionId != null) {
        // Existing option: update optionText if no votes
        final PollOption existingOpt = existingOptionsById.get(optionId);

        if (isNull(existingOpt)) {
          throw PollUpdateCantChangeOptionsException.of();
        }

        if (hasVotes && existingOpt.hasChanged(newText)) {
          throw PollUpdateCantChangeOptionsException.of();
        }

        existingOpt.setOptionText(newText); // Update text
        existingOptionsById.remove(optionId); // Mark as processed
      } else {
        // New option: prepare to add
        optionsToAdd.add(opt);
      }
    }

    // If votes exist, ensure no options were removed
    if (hasVotes && !existingOptionsById.isEmpty()) {
      throw PollUpdateCantChangeOptionsException.of();
    }

    // Remove unlisted options if no votes
    if (!hasVotes) {
      poll.getOptions().removeIf(opt -> existingOptionsById.containsKey(opt.getPollOptionId()));
    }

    // Add new options
    poll.addOptions(optionsToAdd);
  }
}
