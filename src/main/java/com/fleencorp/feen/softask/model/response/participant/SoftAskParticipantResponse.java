package com.fleencorp.feen.softask.model.response.participant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "username"
})
public class SoftAskParticipantResponse {

  @JsonProperty("username")
  private String username;

  public static SoftAskParticipantResponse of(final String username) {
    return new SoftAskParticipantResponse(username);
  }
}
