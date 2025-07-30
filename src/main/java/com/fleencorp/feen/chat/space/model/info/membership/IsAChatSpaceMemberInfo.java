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
  "is_a_member",
  "is_a_member_text"
})
public class IsAChatSpaceMemberInfo {

  @JsonProperty("is_a_member")
  private Boolean isAMember;

  @JsonProperty("is_a_member_text")
  private String isAMemberText;

  public static IsAChatSpaceMemberInfo of(final Boolean isAMember, final String isAMemberText) {
    return new IsAChatSpaceMemberInfo(isAMember, isAMemberText);
  }

  public static IsAChatSpaceMemberInfo of() {
    return new IsAChatSpaceMemberInfo();
  }
}
