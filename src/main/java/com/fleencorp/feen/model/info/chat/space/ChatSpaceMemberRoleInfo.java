package com.fleencorp.feen.model.info.chat.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "role",
  "role_text"
})
public class ChatSpaceMemberRoleInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("role")
  private ChatSpaceMemberRole chatSpaceMemberRole;

  @JsonProperty("role_text")
  private String roleText;

  public static ChatSpaceMemberRoleInfo of(final ChatSpaceMemberRole chatSpaceMemberRole, final String roleText) {
    return ChatSpaceMemberRoleInfo.builder()
      .chatSpaceMemberRole(chatSpaceMemberRole)
      .roleText(roleText)
      .build();
  }

}
