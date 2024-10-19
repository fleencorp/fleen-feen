package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.validator.ValidEmail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddNewEventAttendeeDto {

  @NotBlank(message = "{event.attendee.emailAddress.NotBlank}")
  @Size(min = 6, max = 50, message = "{event.attendee.emailAddress.Size}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @Size(min = 3, max = 100, message = "{event.attendee.aliasOrDisplayName.Size}")
  @ToTitleCase
  @JsonProperty("alias_or_display_name")
  private String aliasOrDisplayName;

  @Size(min = 10, max = 500, message = "{comment.Size}")
  @JsonProperty("comment")
  protected String comment;
}
