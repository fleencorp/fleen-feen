package com.fleencorp.feen.like.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.like.constant.LikeParentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @NotNull(message = "{like.parentType.NotNull}")
  @OneOf(enumClass = LikeParentType.class, message = "{like.parentType.Type}")
  @JsonProperty("like_parent_type")
  private String likeParentType;

  public List<LikeParentType> getLikeParentType() {
   final LikeParentType parentType = LikeParentType.of(likeParentType);

   return nonNull(parentType) ? List.of(parentType) : LikeParentType.all();
  }
}
