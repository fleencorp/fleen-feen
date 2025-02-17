package com.fleencorp.feen.model.info.chat.space.membership;

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
  "is_chat_space_member_removed",
  "is_chat_space_member_removed_text",
})
public class IsChatSpaceMemberRemovedInfo {

  @JsonProperty("is_chat_space_member_removed")
  private Boolean isChatSpaceMemberRemoved;

  @JsonProperty("is_chat_space_member_removed_text")
  private String isChatSpaceMemberRemovedText;

  public static IsChatSpaceMemberRemovedInfo of(final Boolean isChatSpaceMemberRemoved, final String isChatSpaceMemberRemovedText) {
    return new IsChatSpaceMemberRemovedInfo(isChatSpaceMemberRemoved, isChatSpaceMemberRemovedText);
  }

  public static IsChatSpaceMemberRemovedInfo of() {
    return new IsChatSpaceMemberRemovedInfo();
  }
}
