package com.fleencorp.feen.bookmark.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.bookmark.constant.BookmarkParentType;
import com.fleencorp.feen.bookmark.constant.BookmarkType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkDto {

  @NotNull(message = "{bookmark.type.NotNull}")
  @OneOf(enumClass = BookmarkType.class, message = "{bookmark.type.Type}")
  @JsonProperty("type")
  private String bookmarkType;

  @NotNull(message = "{bookmark.parent.NotNull}")
  @JsonProperty("parent")
  private BookmarkParentDto parent;

  private boolean hasParent() {
    return nonNull(parent);
  }

  public Long getParentId() {
    return hasParent() ? parent.getParentId() : null;
  }

  public Long getOtherId() {
    return hasParent() ? parent.getOtherId() :  null;
  }

  public BookmarkParentType getBookmarkParentType() {
    return hasParent() ? BookmarkParentType.of(parent.getBookmarkParentType()) : null;
  }

  public BookmarkType getBookmarkType() {
    return BookmarkType.of(bookmarkType);
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class BookmarkParentDto {

    @NotNull(message = "{bookmark.parentType.NotNull}")
    @OneOf(enumClass = BookmarkParentType.class, message = "{bookmark.parentType.Type}")
    @JsonProperty("parent_type")
    private String bookmarkParentType;

    @NotNull(message = "{bookmark.parentId.NotNull}")
    @IsNumber(message = "{bookmark.parentId.IsNumber}")
    @JsonProperty("parent_id")
    protected String parentId;
    
    @IsNumber(message = "{bookmark.otherId.IsNumber}")
    @JsonProperty("other_id")
    protected String otherId;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public Long getOtherId() {
      return nonNull(otherId) ? Long.parseLong(otherId) : null;
    }
  }
}
