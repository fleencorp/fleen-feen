package com.fleencorp.feen.bookmark.model.response;

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
  "message",
  "parent_total_bookmarks",
  "bookmark"
})
public class BookmarkCreateResponse extends LocalizedResponse {

  @JsonProperty("parent_total_bookmarks")
  private Integer parentTotalBookmarks;

  @JsonProperty("bookmark")
  private BookmarkResponse bookmark;

  @Override
  public String getMessageCode() {
    return nonNull(bookmark) && bookmark.isBookmarked() ? "bookmark.bookmarked" : "bookmark.unbookmarked";
  }

  public static BookmarkCreateResponse of(final BookmarkResponse bookmarkResponse, final Integer parentTotalBookmarks) {
    return new BookmarkCreateResponse(parentTotalBookmarks, bookmarkResponse);
  }
}
