package com.fleencorp.feen.follower.model.info;

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
  "followed",
  "followed_text",
  "followed_text_2",
  "followed_text_3",
  "followed_other_text"
})
public class IsFollowedInfo {

  @JsonProperty("followed")
  private Boolean followed;

  @JsonProperty("followed_text")
  private String followedText;

  @JsonProperty("followed_text_2")
  private String followedText2;

  @JsonProperty("followed_text_3")
  private String followedText3;

  @JsonProperty("followed_other_text")
  private String followedOtherText;

  public static IsFollowedInfo of(final Boolean followed, final String followedText, final String followedText2, final String followedText3, final String followedOtherText) {
    return new IsFollowedInfo(followed, followedText, followedText2, followedText3, followedOtherText);
  }
}

