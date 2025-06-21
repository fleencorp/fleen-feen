package com.fleencorp.feen.poll.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.domain.PollOption;
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
import java.util.List;

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

  @NotNull(message = "{user.visibility.NotNull}")
  @OneOf(enumClass = PollVisibility.class, message = "{user.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  private String visibility;

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

  public Poll toChatSpacePoll(final ChatSpace chatSpace, final Member member) {
    final Poll poll = toPoll(member);
    poll.setPollParentType(PollParentType.CHAT_SPACE);

    poll.setParentId(chatSpace.getChatSpaceId());
    poll.setParentTitle(chatSpace.getTitle());
    poll.setChatSpace(chatSpace);
    poll.setChatSpaceId(chatSpace.getChatSpaceId());

    return poll;
  }

  public Poll toStreamPoll(final FleenStream stream, final Member member) {
    final Poll poll = toPoll(member);
    poll.setPollParentType(PollParentType.STREAM);

    poll.setParentId(stream.getStreamId());
    poll.setParentTitle(stream.getTitle());
    poll.setStream(stream);
    poll.setStreamId(stream.getStreamId());

    return poll;
  }

  public Poll toPoll(final Member author) {
    final Poll poll = new Poll();
    poll.setQuestion(question);
    poll.setDescription(description);
    poll.setExpiresAt(getExpiresAt());
    poll.setMultipleChoice(isMultipleChoice());
    poll.setAnonymous(isAnonymous());
    poll.setVisibility(getVisibility());
    poll.setAuthorId(author.getMemberId());
    poll.setAuthor(author);


    final Collection<PollOption> pollOptions = toPollOptions();
    poll.addOptions(pollOptions);

    return poll;
  }

  protected Collection<PollOption> toPollOptions() {
    final List<PollOption> pollOptions = new ArrayList<>();

    for (final PollOptionDto option : options) {
      final PollOption pollOption = new PollOption();
      pollOption.setPoll(poll);
      pollOption.setPollOptionId(option.getPollOptionId());
      pollOption.setOptionText(option.getOptionText());
      pollOptions.add(pollOption);
    }

    return pollOptions;
  }


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
  }
}

