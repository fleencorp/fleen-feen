package com.fleencorp.feen.model.dto.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.like.LikeParentType;
import com.fleencorp.feen.constant.like.LikeType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.like.Like;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

  @IsNumber(message = "like.parentId.IsNumber")
  @JsonProperty("parent_id")
  protected String parentId;

  @NotNull(message = "{like.parentType.NotNull}")
  @OneOf(enumClass = LikeParentType.class, message = "{like.parentType.Type}")
  @JsonProperty("like_parent_type")
  private String likeParentType;

  @NotNull(message = "{like.type.NotNull}")
  @OneOf(enumClass = LikeType.class, message = "{like.type.Type}")
  @JsonProperty("like_type")
  private String likeType;

  public Long getParentId() {
    return Long.parseLong(parentId);
  }

  public LikeParentType getLikeParentType() {
    return LikeParentType.of(likeParentType);
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

  protected Like toStreamLike(final FleenStream stream, final Member member) {
    final Like like = toLike(stream.getStreamId(), stream.getTitle(), member);
    like.setStreamId(stream.getStreamId());
    like.setStream(stream);

    return like;
  }

  protected Like toChatSpaceLike(final ChatSpace chatSpace, final Member member) {
    final Like like = toLike(chatSpace.getChatSpaceId(), chatSpace.getTitle(), member);
    like.setChatSpaceId(chatSpace.getChatSpaceId());
    like.setChatSpace(chatSpace);

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
}
