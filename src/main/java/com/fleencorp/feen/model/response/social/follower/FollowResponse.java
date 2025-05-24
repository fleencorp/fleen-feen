package com.fleencorp.feen.model.response.social.follower;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.contract.UserFollowStat;
import com.fleencorp.feen.model.info.user.profile.IsFollowingInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowedInfo;
import com.fleencorp.feen.model.info.user.profile.TotalFollowingInfo;
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
  "is_following_info",
  "total_followed_info",
  "total_following_info"
})
public class FollowResponse extends LocalizedResponse implements UserFollowStat {

  @JsonProperty("is_following_info")
  private IsFollowingInfo isFollowingInfo;

  @JsonProperty("total_followed_info")
  private TotalFollowedInfo totalFollowedInfo;

  @JsonProperty("total_following_info")
  private TotalFollowingInfo totalFollowingInfo;

  @Override
  public String getMessageCode() {
    return "";
  }
}
