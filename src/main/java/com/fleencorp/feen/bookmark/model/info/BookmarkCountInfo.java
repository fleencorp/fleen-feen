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
  "bookmark_count",
  "bookmark_text"
})
public class BookmarkCountInfo {

  @JsonProperty("bookmark_count")
  private Integer bookmarkCount;

  @JsonProperty("bookmark_text")
  private String bookmarkText;

  public static BookmarkCountInfo of(final Integer bookmarkCount, final String bookmarkText) {
    return new BookmarkCountInfo(bookmarkCount, bookmarkText);
  }
}
