package com.fleencorp.feen.bookmark.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
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
  "id",
  "parent_info",
  "user_bookmark_info",
  "parent_type",
  "type",
  "is_bookmarked",
  "other_id",
  "created_on",
  "updated_on",
})
public class BookmarkResponse extends FleenFeenResponse {

  @JsonProperty("parent_info")
  private ParentInfo parentInfo;

  @JsonProperty("user_bookmark_info")
  private UserBookmarkInfo userBookmarkInfo;

  @JsonFormat(shape = STRING)
  @JsonProperty("parent_type")
  private BookmarkParentType bookmarkParentType;

  @JsonProperty("other_id")
  private Long otherId;

  @JsonFormat(shape = STRING)
  @JsonProperty("type")
  private BookmarkType bookmarkType;

  @JsonProperty("is_bookmarked")
  public boolean isBookmarked() {
    return BookmarkType.isBookmarked(bookmarkType);
  }
}
