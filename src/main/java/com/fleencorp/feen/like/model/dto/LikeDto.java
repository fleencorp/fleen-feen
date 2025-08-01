package com.fleencorp.feen.like.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
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
public class LikeDto {

  @NotNull(message = "{like.type.NotNull}")
  @OneOf(enumClass = LikeType.class, message = "{like.type.Type}")
  @JsonProperty("type")
  private String likeType;

  @NotNull(message = "{like.parent.NotNull}")
  @JsonProperty("parent")
  private LikeParentDto parent;

  public Long getParentId() {
    return nonNull(parent) ? parent.getParentId() : null;
  }

  public LikeParentType getLikeParentType() {
    return nonNull(parent) ? LikeParentType.of(parent.getLikeParentType()) : null;
  }

  public LikeType getLikeType() {
    return LikeType.of(likeType);
  }

  public Like by(final FleenStream stream, final ChatSpace chatSpace, final Member member) {
    if (LikeParentType.isStream(getLikeParentType())) {
      return toStreamLike(stream, member);
    } else if (LikeParentType.isChatSpace(getLikeParentType())) {
      return toChatSpaceLike(chatSpace, member);
    }

    throw FailedOperationException.of();
  }

  protected Like toChatSpaceLike(final ChatSpace chatSpace, final Member member) {
    final Like like = toLike(chatSpace.getChatSpaceId(), chatSpace.getTitle(), member);
    like.setChatSpaceId(chatSpace.getChatSpaceId());
    like.setChatSpace(chatSpace);

    return like;
  }

  protected Like toStreamLike(final FleenStream stream, final Member member) {
    final Like like = toLike(stream.getStreamId(), stream.getTitle(), member);
    like.setStreamId(stream.getStreamId());
    like.setStream(stream);

    return like;
  }

  protected Like toLike(final Long parentId, final String parentTitle, final Member member) {
    final Like like = new Like();
    like.setParentId(parentId);
    like.setParentTitle(parentTitle);
    like.setLikeParentType(getLikeParentType());
    like.setLikeType(getLikeType());
    like.setMemberId(member.getMemberId());
    like.setMember(member);

    return like;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class LikeParentDto {

    @NotNull(message = "{like.parentType.NotNull}")
    @OneOf(enumClass = LikeParentType.class, message = "{like.parentType.Type}")
    @JsonProperty("parent_type")
    private String likeParentType;

    @NotNull(message = "{like.parentId.NotNull}")
    @IsNumber(message = "{like.parentId.IsNumber}")
    @JsonProperty("parent_id")
    protected String parentId;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }
  }
}
