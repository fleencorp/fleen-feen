package com.fleencorp.feen.poll.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollVoteSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("option_id")
  private String pollOptionId;

  public boolean hasOptionId() {
    return nonNull(pollOptionId) && !pollOptionId.isEmpty();
  }

  public Long getPollOptionId() {
    return Long.parseLong(pollOptionId);
  }
}
