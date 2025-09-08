package com.fleencorp.feen.poll.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPollDto {

  @NotBlank(message = "{poll.question.NotBlank}")
  @Size(min = 1, max = 1000, message = "{poll.question.Size}")
  @JsonProperty("question")
  private String question;

  @NotBlank(message = "{poll.description.NotBlank}")
  @Size(min = 1, max = 2000, message = "{poll.description.Size}")
  @JsonProperty("description")
  private String description;

  @Valid
  @NotEmpty(message = "{poll.options.NotEmpty}")
  @Size(min = 2, max = 10, message = "{poll.options.Size}")
  @JsonProperty("options")
  private Collection<PollOptionDto> options = new ArrayList<>();

  @NotNull(message = "{poll.visibility.NotNull}")
  @OneOf(enumClass = PollVisibility.class, message = "{poll.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  private String visibility;

  @JsonProperty("parent")
  private PollParentDto parent;

  @NotNull(message = "{poll.multipleChoice.NotNull}")
  @ValidBoolean
  @JsonProperty("is_multiple_choice")
  protected String isMultipleChoice;

  @NotNull(message = "{poll.anonymous.NotNull}")
  @ValidBoolean
  @JsonProperty("is_anonymous")
  protected String isAnonymous;

  @NotNull(message = "{poll.expiresAt.NotNull}")
  @DateTimeValid
  @FutureDate
  @JsonProperty("expires_at")
  protected String expiresAt;

  public LocalDateTime getExpiresAt() {
    return LocalDateTime.parse(expiresAt);
  }

  public boolean isMultipleChoice() {
    return parseBoolean(isMultipleChoice);
  }

  public boolean isAnonymous() {
    return parseBoolean(isAnonymous);
  }

  public PollVisibility getVisibility() {
    return PollVisibility.of(visibility);
  }

  public boolean hasParent() {
    return nonNull(parent) && nonNull(parent.getParentId()) && nonNull(parent.getParentType());
  }

  public Long getParentId() {
    return nonNull(parent) ? parent.getParentId() : null;
  }

  public PollParentType getParentType() {
    return hasParent() ? parent.getParentType() : null;
  }

  public boolean isChatSpaceParent() {
    return hasParent() && parent.isChatSpaceParent();
  }

  public boolean isStreamParent() {
    return hasParent() && parent.isStreamParent();
  }

  public Poll toPoll(final Member author, final String parentTitle, final ChatSpace chatSpace, final FleenStream stream) {
    final Long parentId = getParentId();
    final PollParentType parentType = getParentType();

    final Poll poll = new Poll();
    poll.setQuestion(question);
    poll.setDescription(description);
    poll.setExpiresAt(getExpiresAt());

    poll.setMultipleChoice(isMultipleChoice());
    poll.setAnonymous(isAnonymous());
    poll.setVisibility(getVisibility());

    poll.setAuthorId(author.getMemberId());
    poll.setAuthor(author);

    poll.setParentId(parentId);
    poll.setParentTitle(parentTitle);
    poll.setPollParentType(parentType);

    poll.setChatSpaceId(parentId);
    poll.setChatSpace(chatSpace);

    poll.setStreamId(parentId);
    poll.setStream(stream);

    final Collection<PollOption> pollOptions = toPollOptions();
    poll.addOptions(pollOptions);

    return poll;
  }

  protected Collection<PollOption> toPollOptions() {
    final Collection<PollOption> pollOptions = new ArrayList<>();

    for (final PollOptionDto option : options) {
      final PollOption pollOption = option.toPollOption();
      pollOptions.add(pollOption);
    }

    return pollOptions;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class PollOptionDto {

    @IsNumber
    @JsonProperty("poll_option_id")
    private String pollOptionId;

    @Size(min = 1, max = 1000, message = "{poll.option.Size}")
    @Pattern(regexp = "\\S.*\\S", message = "{poll.option.Invalid}")
    @JsonProperty("option_text")
    private String optionText;

    public Long getPollOptionId() {
      return nonNull(pollOptionId) ? Long.parseLong(pollOptionId) : null;
    }

    public PollOption toPollOption() {
      final PollOption pollOption = new PollOption();
      pollOption.setPollOptionId(getPollOptionId());
      pollOption.setOptionText(getOptionText());

      return pollOption;
    }
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class PollParentDto {

    @IsNumber(message = "{poll.parentId.IsNumber}")
    @JsonProperty("parent_id")
    private String parentId;

    @OneOf(enumClass = PollParentType.class, message = "{poll.parentType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("parent_type")
    private String parentType;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public PollParentType getParentType() {
      return PollParentType.of(parentType);
    }

    public boolean isChatSpaceParent() {
      return PollParentType.isChatSpace(getParentType());
    }

    public boolean isStreamParent() {
      return PollParentType.isStream(getParentType());
    }
  }
}

