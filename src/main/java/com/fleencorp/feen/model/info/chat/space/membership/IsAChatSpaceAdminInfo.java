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
  "is_admin",
  "is_admin_text",
  "is_admin_text_2"
})
public class IsAChatSpaceAdminInfo {

  @JsonProperty("is_admin")
  private Boolean isAdmin;

  @JsonProperty("is_admin_text")
  private String isAdminText;

  @JsonProperty("is_admin_text_2")
  private String isAdminText2;

  public static IsAChatSpaceAdminInfo of(final Boolean isAdmin, final String isAdminText, final String isAdminText2) {
    return new IsAChatSpaceAdminInfo(isAdmin, isAdminText, isAdminText2);
  }

  public static IsAChatSpaceAdminInfo of() {
    return new IsAChatSpaceAdminInfo();
  }
}
