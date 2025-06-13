package com.fleencorp.feen.block.user.mapper.impl;

import com.fleencorp.feen.block.user.mapper.BlockUserMapper;
import com.fleencorp.feen.block.user.model.domain.BlockUser;
import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
import com.fleencorp.feen.block.user.model.response.BlockUserResponse;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class BlockUserMapperImpl extends BaseMapper implements BlockUserMapper {

  private final UnifiedMapper unifiedMapper;

  public BlockUserMapperImpl(
      final UnifiedMapper unifiedMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.unifiedMapper = unifiedMapper;
  }

  /**
   * Converts a single {@link BlockUser} entity to a {@link BlockUserResponse} DTO.
   *
   * <p>It maps the blocked member's full name and username, and uses {@link UnifiedMapper#toHasBlockedInfo}
   * to construct a localized description of the blocking status.</p>
   *
   * @param entry the {@link BlockUser} entity to convert
   * @return a {@link BlockUserResponse} containing user information and block status; {@code null} if the input is {@code null}
   */
  @Override
  public BlockUserResponse toBlockUserResponse(final BlockUser entry) {
    if (nonNull(entry)) {
      final HasBlockedInfo hasBlockedInfo = unifiedMapper.toHasBlockedInfo(entry.isBlocked(), entry.getBlockedMemberName());

      final BlockUserResponse response = new BlockUserResponse();
      response.setFullName(entry.getBlockedMemberName());
      response.setUsername(entry.getBlockedMemberUsername());
      response.setHasBlockedInfo(hasBlockedInfo);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link BlockUser} entities to a collection of {@link BlockUserResponse} DTOs.
   *
   * <p>Each non-null entry is mapped using {@link #toBlockUserResponse(BlockUser)}. If the input is {@code null}
   * or empty, an empty list is returned.</p>
   *
   * @param entries a collection of {@link BlockUser} entities to convert
   * @return a list of {@link BlockUserResponse} DTOs, or an empty list if input is {@code null} or empty
   */
  @Override
  public Collection<BlockUserResponse> toBlockUserResponse(final Collection<BlockUser> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toBlockUserResponse)
        .toList();
    }
    return List.of();
  }
}
