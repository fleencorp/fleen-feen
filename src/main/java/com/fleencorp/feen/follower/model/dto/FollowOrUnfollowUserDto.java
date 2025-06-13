package com.fleencorp.feen.follower.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
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
public class FollowOrUnfollowUserDto {

  @NotNull(message = "{chat.space.member.NotNull}")
  @IsNumber
  @JsonProperty("user_id")
  private String userId;

  public Long getMemberId() {
    return Long.parseLong(userId);
  }

  public Member getMember() {
    return Member.of(getMemberId());
  }
}
