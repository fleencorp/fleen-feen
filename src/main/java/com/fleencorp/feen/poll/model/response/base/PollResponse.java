package com.fleencorp.feen.poll.model.response.base;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.model.contract.Updatable;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.poll.model.info.*;
import com.fleencorp.feen.user.model.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "question",
  "description",
  "poll_visibility_info",
  "expires_at",
  "total_entries",
  "total_vote_entries_info",
  "is_multiple_choice_info",
  "is_anonymous_info",
  "is_ended_info",
  "is_updatable",
  "poll_options",
  "poll_vote",
  "author",
})
public class PollResponse extends FleenFeenResponse
    implements Updatable {

  @JsonProperty("question")
  private String question;

  @JsonProperty("description")
  private String description;

  @JsonProperty("poll_visibility_info")
  private PollVisibilityInfo pollVisibilityInfo;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("expires_at")
  private LocalDateTime expiresAt;

  @JsonProperty("is_multiple_choice_info")
  private IsMultipleChoiceInfo isMultipleChoiceInfo;

  @JsonProperty("is_anonymous_info")
  private IsAnonymousInfo isAnonymousInfo;

  @JsonProperty("is_ended_info")
  private IsEndedInfo isEndedInfo;

  @JsonProperty("total_vote_entries_info")
  private TotalPollVoteEntriesInfo totalPollVoteEntriesInfo;

  @JsonProperty("poll_options")
  private Collection<PollOptionResponse> pollOptions = new ArrayList<>();

  @JsonProperty("poll_vote")
  private PollVoteResponse pollVote;

  @JsonProperty("author")
  private UserResponse author;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long organizerId;

  @Override
  @JsonIgnore
  public Long getAuthorId() {
    return getOrganizerId();
  }

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }

}
