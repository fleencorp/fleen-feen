package com.fleencorp.feen.poll.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.exception.poll.PollUpdateUnauthorizedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollDeletedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollEndedException;
import com.fleencorp.feen.poll.exception.vote.PollVotingNotAllowedPollNoOptionException;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "poll")
public class Poll extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "poll_id", nullable = false, updatable = false, unique = true)
  private Long pollId;

  @Column(name = "question", nullable = false, length = 1000)
  private String question;

  @Column(name = "description", length = 2000)
  private String description;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", length = 1000, updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "parent_type", nullable = false)
  private PollParentType pollParentType = PollParentType.NONE;

  @Enumerated(STRING)
  @Column(name = "visibility", nullable = false)
  private PollVisibility visibility = PollVisibility.PUBLIC;

  @Column(name = "author_id", insertable = false, updatable = false)
  private Long authorId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private Member author;

  @Column(name = "stream_id", insertable = false, updatable = false)
  private Long streamId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id", updatable = false)
  private FleenStream stream;

  @Column(name = "chat_space_id", insertable = false, updatable = false)
  private Long chatSpaceId;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", updatable = false)
  private ChatSpace chatSpace;

  @OneToMany(mappedBy = "poll", fetch = EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<PollOption> options = new HashSet<>();

  @Column(name = "is_anonymous", nullable = false)
  private boolean isAnonymous = false;

  @Column(name = "is_multiple_choice", nullable = false)
  private Boolean multipleChoice = false;

  @Column(name = "deleted", nullable = false)
  private Boolean deleted = false;

  @Column(name = "total_entries", nullable = false)
  private Integer totalEntries = 0;

  public void update(
      final String question,final String description, final LocalDateTime expiresAt,
      final PollVisibility pollVisibility, final Boolean isAnonymous, final Boolean isMultipleChoice) {
    this.question = question;
    this.description = description;
    this.expiresAt = expiresAt;
    this.visibility = pollVisibility;
    this.isAnonymous = isAnonymous;
    this.multipleChoice = isMultipleChoice;
  }

  /**
   * Adds an option to the poll.
   *
   * @param option the option to add
   */
  public void addOption(final PollOption option) {
    if (nonNull(options)) {
      option.setPoll(this);
      options.add(option);
    }
  }

  public void addOptions(final Collection<PollOption> options) {
    if (nonNull(options)) {
      options.forEach(this::addOption);
    }
  }

  public void delete() {
    deleted = true;
  }

  public Collection<Long> getPollOptionIds() {
    return options.stream()
      .filter(Objects::nonNull)
      .map(PollOption::getPollOptionId)
      .collect(Collectors.toSet());
  }

  public static Collection<Long> getOptionIds(final Collection<PollOption> pollOptions) {
    return isNull(pollOptions)
      ? new ArrayList<>()
      : pollOptions.stream()
      .filter(Objects::nonNull)
      .map(PollOption::getPollOptionId)
      .filter(Objects::nonNull)
      .collect(Collectors.toSet());
  }

  public Map<Long, PollOption> getOptionsGrouped() {
    return options.stream()
      .filter(opt -> opt.getPollOptionId() != null)
      .collect(Collectors.toMap(PollOption::getPollOptionId, Function.identity()));
  }

  public Map<Long, PollOption> getOptionsGroupedCopy() {
    return getOptionsGrouped().entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public boolean isDeleted() {
    return nonNull(deleted) && deleted;
  }

  public boolean isMultipleChoice() {
    return nonNull(multipleChoice) && multipleChoice;
  }

  public boolean isSingleChoice() {
    return !isMultipleChoice();
  }

  public boolean hasAChatSpaceParent() {
    return nonNull(chatSpace);
  }

  public boolean hasAStreamParent() {
    return nonNull(stream);
  }

  public boolean hasNoParent() {
    return nonNull(parentId) && PollParentType.isNone(pollParentType);
  }

  public boolean hasEnded() {
    return nonNull(expiresAt) && expiresAt.isBefore(LocalDateTime.now());
  }

  public boolean hasNoOptions() {
    return nonNull(options) && options.isEmpty();
  }

  public void checkAuthor(final Long userId) {
    if (!this.author.getMemberId().equals(userId)) {
      throw new PollUpdateUnauthorizedException(pollId);
    }
  }

  /**
   * Validates whether the specified {@link Poll} is eligible for voting.
   *
   * <p>This method checks if the poll is deleted, has ended, or has no available options.
   * If the poll is deleted, it throws {@link PollVotingNotAllowedPollDeletedException}.
   * If the poll has ended, it throws {@link PollVotingNotAllowedPollEndedException}.
   * If the poll has no options, it throws {@link PollVotingNotAllowedPollNoOptionException}.</p>
   *
   * @throws PollVotingNotAllowedPollDeletedException if the poll has been deleted
   * @throws PollVotingNotAllowedPollEndedException if the poll has ended
   * @throws PollVotingNotAllowedPollNoOptionException if the poll has no options
   */
  public void validatePollForVote() {
    if (isDeleted()) {
      throw PollVotingNotAllowedPollDeletedException.of(pollId);
    } else if (hasEnded()) {
      throw PollVotingNotAllowedPollEndedException.of(pollId);
    } else if (hasNoOptions()) {
      throw PollVotingNotAllowedPollNoOptionException.of(pollId);
    }
  }

  public static Poll of(final Long pollId) {
    final Poll poll = new Poll();
    poll.setPollId(pollId);
    return poll;
  }
}

