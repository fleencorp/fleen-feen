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
  "following",
  "following_text",
  "following_text_2",
  "following_text_3",
  "following_other_text"
})
public class IsFollowingInfo {

  @JsonProperty("following")
  private Boolean following;

  @JsonProperty("following_text")
  private String followingText;

  @JsonProperty("following_text_2")
  private String followingText2;

  @JsonProperty("following_text_3")
  private String followingText3;

  @JsonProperty("following_other_text")
  private String followingOtherText;

  public static IsFollowingInfo of(final Boolean following, final String followingText, final String followingText2, final String followingText3, final String followingOtherText) {
    return new IsFollowingInfo(following, followingText, followingText2, followingText3, followingOtherText);
  }
}

