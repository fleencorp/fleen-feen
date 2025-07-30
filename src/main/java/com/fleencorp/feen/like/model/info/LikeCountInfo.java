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
  "like_count",
  "like_text"
})
public class LikeCountInfo {

  @JsonProperty("like_count")
  private Integer likeCount;

  @JsonProperty("like_text")
  private String likeText;

  public static LikeCountInfo of(final Integer likeCount, final String likeText) {
    return new LikeCountInfo(likeCount, likeText);
  }
}
