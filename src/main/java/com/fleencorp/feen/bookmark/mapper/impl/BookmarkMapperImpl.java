package com.fleencorp.feen.bookmark.mapper.impl;

import com.fleencorp.feen.bookmark.mapper.BookmarkMapper;
import com.fleencorp.feen.bookmark.model.domain.Bookmark;
import com.fleencorp.feen.bookmark.model.info.UserBookmarkInfo;
import com.fleencorp.feen.bookmark.model.response.BookmarkResponse;
import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.impl.info.ToInfoMapperImpl;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Component
public class BookmarkMapperImpl extends BaseMapper implements BookmarkMapper {

  private final ToInfoMapper toInfoMapper;

  public BookmarkMapperImpl(final MessageSource messageSource) {
    super(messageSource);
    this.toInfoMapper = new ToInfoMapperImpl(messageSource);
  }

  /**
   * Converts a {@link Bookmark} entity into a {@link BookmarkResponse} object.
   *
   * <p>This method maps all relevant fields from the given {@code Bookmark} entity
   * into a new {@code BookmarkResponse} instance. It includes identifiers, timestamps,
   * type, parent information, and user-specific bookmark details. If the input is
   * {@code null}, the method returns {@code null}.</p>
   *
   * @param entry the {@link Bookmark} entity to be converted; may be {@code null}
   * @return a {@link BookmarkResponse} populated with the mapped values from the given
   *         {@code Bookmark}, or {@code null} if the input is {@code null}
   */
  @Override
  public BookmarkResponse toBookmarkResponse(final Bookmark entry) {
    if (nonNull(entry)) {
      final BookmarkResponse response = new BookmarkResponse();
      response.setId(entry.getBookmarkId());
      response.setBookmarkType(entry.getBookmarkType());
      response.setOtherId(entry.getOtherId());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getParentSummary());
      response.setParentInfo(parentInfo);

      final UserBookmarkInfo userBookmarkInfo = toInfoMapper.toBookmarkInfo(entry.isBookmarked());
      response.setUserBookmarkInfo(userBookmarkInfo);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link Bookmark} entities into a collection of {@link BookmarkResponse} objects.
   *
   * <p>This method safely handles {@code null} or empty input by returning an empty collection.
   * Each non-null {@link Bookmark} entity in the provided collection is mapped to a
   * corresponding {@link BookmarkResponse} using {@link #toBookmarkResponse(Bookmark)}.</p>
   *
   * @param entries the collection of {@link Bookmark} entities to be converted; may be {@code null}
   * @return a collection of {@link BookmarkResponse} objects corresponding to the input entities,
   *         or an empty collection if the input is {@code null} or contains no valid entities
   */
  @Override
  public Collection<BookmarkResponse> toBookmarkResponses(final Collection<Bookmark> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toBookmarkResponse)
      .toList();
  }
}
