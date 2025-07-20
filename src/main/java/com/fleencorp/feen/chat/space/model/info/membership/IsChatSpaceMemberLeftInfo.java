package com.fleencorp.feen.chat.space.model.info.membership;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "is_chat_space_member_left",
  "is_chat_space_member_left_text",
})
public class IsChatSpaceMemberLeftInfo {

  @JsonProperty("is_chat_space_member_left")
  private Boolean isChatSpaceMemberLeft;

  @JsonProperty("is_chat_space_member_left_text")
  private String isChatSpaceMemberLeftText;

  public static IsChatSpaceMemberLeftInfo of(final Boolean isChatSpaceMemberRemoved, final String isChatSpaceMemberRemovedText) {
    return new IsChatSpaceMemberLeftInfo(isChatSpaceMemberRemoved, isChatSpaceMemberRemovedText);
  }

  public static IsChatSpaceMemberLeftInfo of() {
    return new IsChatSpaceMemberLeftInfo();
  }
}

