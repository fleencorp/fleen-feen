package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.constant.external.google.calendar.calendar.AclScopeType;
import com.fleencorp.feen.converter.common.ToLowerCase;
import com.fleencorp.feen.converter.common.ToUpperCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Builder
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
  @ValidEnum(enumClass = AclScopeType.class, message = "{calendar.aclScopeType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("acl_scope_type")
  private String aclScopeType;

  @NotNull(message = "{calendar.aclRole.NotNull}")
  @ValidEnum(enumClass = AclRole.class, message = "{calendar.aclRole.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("acl_role")
  private String aclRole;

  public AclRole getActualAclRole() {
    return parseEnumOrNull(aclRole, AclRole.class);
  }

  public AclScopeType getActualAclScopeType() {
    return parseEnumOrNull(aclScopeType, AclScopeType.class);
  }
}
