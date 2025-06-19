package com.fleencorp.feen.like.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
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
  "parent_id",
  "parent_title",
  "parent_total_likes",
  "user_like_info"
})
public class LikeResponse {

  @JsonProperty("parent_id")
  private Long parentId;

  @JsonProperty("parent_title")
  private String parentTitle;

  @JsonProperty("parent_total_likes")
  private Long parentTotalLikes;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  public static LikeResponse of(final Long parentId, final String parentTitle, final Long parentTotalLikes, final UserLikeInfo userLikeInfo) {
    return new LikeResponse(parentId, parentTitle, parentTotalLikes, userLikeInfo);
  }
}
