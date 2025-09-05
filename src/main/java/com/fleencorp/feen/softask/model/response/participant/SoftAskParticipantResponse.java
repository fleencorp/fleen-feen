package com.fleencorp.feen.softask.model.response.participant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "username",
  "display_name",
  "avatar_urls"
})
public class SoftAskParticipantResponse {

  @JsonProperty("username")
  private String username;

  @JsonProperty("display_name")
  private String displayName;

  @JsonProperty("avatar_urls")
  private Map<String, String> avatarUrls;

  public static SoftAskParticipantResponse of(final String username, final String displayName, final Map<String, String> avatarUrls) {
    return new SoftAskParticipantResponse(username, displayName, avatarUrls);
  }
}
