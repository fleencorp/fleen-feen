package com.fleencorp.feen.model.response.like;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.like.LikeInfo;
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
  "total_likes",
  "parent_id",
  "parent_title",
  "like_info"
})
public class LikeResponse {

  @JsonProperty("total_likes")
  private Long totalLikes;

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("parent_title")
  private String parentTitle;

  @JsonProperty("like_info")
  private LikeInfo likeInfo;

  public static LikeResponse of(final Long totalLikes, final Long parentId, final String parentTitle, final LikeInfo likeInfo) {
    return new LikeResponse(totalLikes, parentId, parentTitle, likeInfo);
  }
}
