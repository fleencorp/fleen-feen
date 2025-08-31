package com.fleencorp.feen.like.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "like",
  "parent_total_likes"
})
public class LikeCreateResponse extends LocalizedResponse {

  @JsonProperty("like")
  private LikeResponse like;

  @JsonProperty("parent_total_likes")
  private Integer parentTotalLikes;

  @Override
  public String getMessageCode() {
    return nonNull(like) && like.isLiked() ? "like.liked" : "like.unliked";
  }

  public static LikeCreateResponse of(final LikeResponse likeResponse, final Integer parentTotalLikes) {
    return new LikeCreateResponse(likeResponse, parentTotalLikes);
  }
}
