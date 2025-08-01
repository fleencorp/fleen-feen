package com.fleencorp.feen.chat.space.model.dto.join.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRequestToJoinChatSpaceDto {

  @NotNull(message = "{chat.space.member.NotNull}")
  @IsNumber
  @JsonProperty("chat_space_member_id")
  private String chatSpaceMemberId;

  @NotNull(message = "{chatSpace.joinStatus.NotNull}")
  @OneOf(enumClass = ChatSpaceRequestToJoinStatus.class, message = "{chatSpace.joinStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("join_status")
  private String joinStatus;

  @Size(min = 10, max = 500, message = "{comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public ChatSpaceRequestToJoinStatus getJoinStatus() {
    return ChatSpaceRequestToJoinStatus.of(joinStatus);
  }

  public boolean isApproved() {
    return ChatSpaceRequestToJoinStatus.isApproved(getJoinStatus());
  }

  public boolean isDisapproved() {
    return ChatSpaceRequestToJoinStatus.isDisapproved(getJoinStatus());
  }

  public Long getChatSpaceMemberId() {
    return Long.parseLong(chatSpaceMemberId);
  }
}
