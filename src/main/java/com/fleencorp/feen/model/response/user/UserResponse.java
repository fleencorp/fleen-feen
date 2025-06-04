package com.fleencorp.feen.model.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.user.profile.IsFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.IsFollowingInfo;
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
  "profile_photo_url",
  "is_following_info",
  "is_followed_info"
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

  @JsonProperty("is_following_info")
  private IsFollowingInfo isFollowingInfo;

  @JsonProperty("is_followed_info")
  private IsFollowedInfo isFollowedInfo;

  public static UserResponse of(final Long userId, final String username, final String fullName, final String profilePhotoUrl) {
    final UserResponse userResponse = new UserResponse();
    userResponse.setUserId(userId);
    userResponse.setUsername(username);
    userResponse.setFullName(fullName);
    userResponse.setProfilePhotoUrl(profilePhotoUrl);

    return userResponse;
  }
}
