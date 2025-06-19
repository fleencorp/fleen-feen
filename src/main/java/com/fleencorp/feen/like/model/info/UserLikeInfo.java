package com.fleencorp.feen.like.model.info;

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
  "liked",
  "like_other_text"
})
public class UserLikeInfo {

  @JsonProperty("liked")
  private Boolean liked;

  @JsonProperty("like_other_text")
  private String likeOtherText;

  public static UserLikeInfo of(final boolean liked, final String likeOtherText) {
    return new UserLikeInfo(liked, likeOtherText);
  }

  public static UserLikeInfo of() {
    return new UserLikeInfo();
  }
}
