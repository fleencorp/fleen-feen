package com.fleencorp.feen.model.info.user.profile;

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
  "total_followed",
  "total_followed_text",
  "total_following_text_2"
})
public class TotalFollowedInfo {

  @JsonProperty("total_following")
  private Long totalFollowing;

  @JsonProperty("total_following_text")
  private String totalFollowingText;

  @JsonProperty("total_following_text_2")
  private String totalFollowingText2;

  public static TotalFollowedInfo of(final Long totalFollowing, final String totalFollowingText, final String totalFollowingText2) {
    return new TotalFollowedInfo(totalFollowing, totalFollowingText, totalFollowingText2);
  }
}
