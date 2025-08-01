package com.fleencorp.feen.chat.space.model.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestoreChatSpaceMemberDto {

  @NotNull(message = "{chat.space.member.NotNull}")
  @IsNumber
  @JsonProperty("chat_space_member_id")
  private String chatSpaceMemberId;

  public Long getChatSpaceMemberId() {
    return Long.parseLong(chatSpaceMemberId);
  }

}
