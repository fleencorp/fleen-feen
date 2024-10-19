package com.fleencorp.feen.model.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeChatSpaceMemberToAdminDto {

  @NotNull(message = "{chat.space.member.NotNull}")
  @IsNumber
  @JsonProperty("chat_space_member_id")
  private String chatSpaceMemberId;

  public Long getActualChatSpaceMemberId() {
    return Long.parseLong(chatSpaceMemberId);
  }
}
