package com.fleencorp.feen.poll.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.model.response.core.PollResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "poll_id",
  "poll"
})
public class PollUpdateResponse extends LocalizedResponse {

  @JsonProperty("poll_id")
  private Long pollId;

  @JsonProperty("poll")
  private PollResponse poll;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "poll.update";
  }

  public static PollUpdateResponse of(final Long pollId, final PollResponse pollResponse) {
    return new PollUpdateResponse(pollId, pollResponse);
  }
}
