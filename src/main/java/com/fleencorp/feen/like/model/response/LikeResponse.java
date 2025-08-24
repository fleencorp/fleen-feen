package com.fleencorp.feen.like.model.response;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "parent_info",
  "parent_total_likes",
  "user_like_info",
  "like_parent_type"
})
public class LikeResponse extends FleenFeenResponse {

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("parent_total_likes")
  private Long parentTotalLikes;

  @JsonProperty("user_like_info")
  private UserLikeInfo userLikeInfo;

  @JsonFormat(shape = STRING)
  @JsonProperty("like_parent_type")
  private LikeParentType likeParentType;

  @JsonIgnore
  private LikeType type;

  public boolean isLiked() {
    return LikeType.isLiked(type);
  }
}
