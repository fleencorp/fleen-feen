package com.fleencorp.feen.bookmark.model.info;

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
  "bookmarked",
  "bookmark_other_text"
})
public class UserBookmarkInfo {

  @JsonProperty("bookmarked")
  private Boolean bookmarked;

  @JsonProperty("bookmark_other_text")
  private String bookmarkOtherText;

  public boolean isBookmarked() {
    return bookmarked;
  }

  public static UserBookmarkInfo of(final boolean bookmarked, final String bookmarkOtherText) {
    return new UserBookmarkInfo(bookmarked, bookmarkOtherText);
  }

  public static UserBookmarkInfo of() {
    return new UserBookmarkInfo();
  }
}
