package com.fleencorp.feen.model.response.external.google.chat.membership.base;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleChatSpaceMemberResponse {

  private String name;
  private String state;
  private String role;
  private LocalDateTime createTime;
}
