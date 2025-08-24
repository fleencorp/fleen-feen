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
  "like_text",
  "like_text_2"
})
public class UserLikeInfo {

  @JsonProperty("liked")
  private Boolean liked;

  @JsonProperty("like_text")
  private String likeText;

  @JsonProperty("like_text_2")
  private String likeText2;

  public static UserLikeInfo of(final boolean liked, final String likeText, final String likeText2) {
    return new UserLikeInfo(liked, likeText, likeText2);
  }
}
