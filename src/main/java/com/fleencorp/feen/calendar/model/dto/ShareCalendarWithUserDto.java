package com.fleencorp.feen.calendar.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.common.constant.external.google.calendar.calendar.AclScopeType;
import jakarta.validation.constraints.NotBlank;
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
public class ShareCalendarWithUserDto {

  @NotBlank(message = "{calendar.emailAddress.NotBlank}")
  @Size(max = 50, message = "{calendar.emailAddress.Size}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @NotNull(message = "{calendar.aclScopeType.NotNull}")
  @OneOf(enumClass = AclScopeType.class, message = "{calendar.aclScopeType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("acl_scope_type")
  private String aclScopeType;

  @NotNull(message = "{calendar.aclRole.NotNull}")
  @OneOf(enumClass = AclRole.class, message = "{calendar.aclRole.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("acl_role")
  private String aclRole;

  public AclRole getAclRole() {
    return AclRole.of(aclRole);
  }

  public AclScopeType getAclScopeType() {
    return AclScopeType.of(aclScopeType);
  }
}
