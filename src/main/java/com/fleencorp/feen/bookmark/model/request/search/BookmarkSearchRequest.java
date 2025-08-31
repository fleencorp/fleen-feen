package com.fleencorp.feen.bookmark.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
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
public class BookmarkSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @NotNull(message = "{bookmark.parentType.NotNull}")
  @OneOf(enumClass = BookmarkParentType.class, message = "{bookmark.parentType.Type}")
  @JsonProperty("bookmark_parent_type")
  private String bookmarkParentType;

  public List<BookmarkParentType> getBookmarkParentType() {
   final BookmarkParentType parentType = BookmarkParentType.of(bookmarkParentType);
   return nonNull(parentType) ? List.of(parentType) : BookmarkParentType.all();
  }
}
