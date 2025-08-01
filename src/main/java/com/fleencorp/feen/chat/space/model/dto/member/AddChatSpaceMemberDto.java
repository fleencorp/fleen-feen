package com.fleencorp.feen.chat.space.model.dto.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
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
public class AddChatSpaceMemberDto {

  @NotNull(message = "{chat.space.member.NotNull}")
  @IsNumber
  @JsonProperty("member_id")
  private String memberId;

  @Size(min = 10, max = 500, message = "{comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public Long getMemberId() {
    return Long.parseLong(memberId);
  }
}
