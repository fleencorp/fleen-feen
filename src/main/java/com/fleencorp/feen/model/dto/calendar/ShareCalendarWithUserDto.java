package com.fleencorp.feen.model.dto.calendar;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.external.google.calendar.calendar.AclRole;
import com.fleencorp.feen.constant.external.google.calendar.calendar.AclScopeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareCalendarWithUserDto {

  @NotNull(message = "{calendar.id.NotNull}")
  @IsNumber
  @JsonProperty("calendar_id")
  private Long calendarId;

  @NotBlank(message = "{calendar.emailAddress.NotBlank}")
  @Size(max = 50, message = "{calendar.emailAddress.Size}")
  @ValidEmail
  @JsonProperty("email_address")
  private String emailAddress;


  @NotNull(message = "{calendar.aclScopeType.NotNull}")
  @ValidEnum(enumClass = AclScopeType.class, message = "{calendar.aclScopeType.Type}")
  @JsonProperty("acl_scope_type")
  private String aclScopeType;

  @NotNull(message = "{calendar.aclRole.NotNull}")
  @ValidEnum(enumClass = AclScopeType.class, message = "{calendar.aclRole.Type}")
  @JsonProperty("acl_role")
  private AclRole aclRole;
}
