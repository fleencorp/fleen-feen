package com.fleencorp.feen.chat.space.model.info.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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
    return new ChatSpaceMemberRoleInfo(chatSpaceMemberRole, roleText);
  }

  public static ChatSpaceMemberRoleInfo of() {
    return new ChatSpaceMemberRoleInfo();
  }

}
