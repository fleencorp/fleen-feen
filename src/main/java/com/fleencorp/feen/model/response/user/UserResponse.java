package com.fleencorp.feen.model.response.user;

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
  "user_id",
  "username",
  "full_name",
  "profile_photo_url"
})
public class UserResponse {

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("username")
  private String username;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("profile_photo_url")
  private String profilePhotoUrl;

  public static UserResponse of(final String username, final String fullName, final String profilePhotoUrl) {
    return new UserResponse(null, username, fullName, profilePhotoUrl);
  }
}
