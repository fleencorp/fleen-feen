package com.fleencorp.feen.like.mapper.impl;

import com.fleencorp.feen.common.model.info.ParentInfo;
import com.fleencorp.feen.like.mapper.LikeMapper;
import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.like.model.response.LikeResponse;
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
public class LikeMapperImpl extends BaseMapper implements LikeMapper {

  private final ToInfoMapper toInfoMapper;

  public LikeMapperImpl(final MessageSource messageSource) {
    super(messageSource);
    this.toInfoMapper = new ToInfoMapperImpl(messageSource);
  }

  /**
   * Converts a {@link Like} entity into a {@link LikeResponse} object.
   *
   * <p>This method maps all relevant fields from the given {@code Like} entity
   * into a new {@code LikeResponse} instance. It includes identifiers, timestamps,
   * type, parent information, and user-specific like details. If the input is
   * {@code null}, the method returns {@code null}.</p>
   *
   * @param entry the {@link Like} entity to be converted; may be {@code null}
   * @return a {@link LikeResponse} populated with the mapped values from the given
   *         {@code Like}, or {@code null} if the input is {@code null}
   */
  @Override
  public LikeResponse toLikeResponse(final Like entry) {
    if (nonNull(entry)) {
      final LikeResponse response = new LikeResponse();
      response.setId(entry.getLikeId());
      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());
      response.setType(entry.getLikeType());
      response.setLikeParentType(entry.getLikeParentType());

      final ParentInfo parentInfo = ParentInfo.of(entry.getParentId(), entry.getParentTitle());
      response.setParentInfo(parentInfo);

      final UserLikeInfo userLikeInfo = toInfoMapper.toLikeInfo(entry.isLiked());
      response.setUserLikeInfo(userLikeInfo);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link Like} entities into a collection of {@link LikeResponse} objects.
   *
   * <p>This method safely handles {@code null} or empty input by returning an empty collection.
   * Each non-null {@link Like} entity in the provided collection is mapped to a corresponding
   * {@link LikeResponse} using {@link #toLikeResponse(Like)}.</p>
   *
   * @param entries the collection of {@link Like} entities to be converted; may be {@code null}
   * @return a collection of {@link LikeResponse} objects corresponding to the input entities,
   *         or an empty collection if the input is {@code null} or contains no valid entities
   */
  @Override
  public Collection<LikeResponse> toLikeResponses(final Collection<Like> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toLikeResponse)
      .toList();
  }

  @Override
  public UserLikeInfo toLikeInfo(final boolean isLiked) {
    return toInfoMapper.toLikeInfo(isLiked);
  }
}
