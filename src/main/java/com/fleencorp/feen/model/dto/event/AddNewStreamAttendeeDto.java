package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.constant.stream.StreamType;
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
public class AddNewStreamAttendeeDto {

  @NotBlank(message = "{stream.attendee.emailAddress.NotBlank}")
  @Size(min = 6, max = 50, message = "{stream.attendee.emailAddress.Size}")
  @ValidEmail
  @ToLowerCase
  @JsonProperty("email_address")
  private String emailAddress;

  @Size(min = 3, max = 100, message = "{stream.attendee.aliasOrDisplayName.Size}")
  @ToTitleCase
  @JsonProperty("alias_or_display_name")
  private String aliasOrDisplayName;

  @Size(min = 10, max = 500, message = "{comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  @NotNull(message = "{stream.streamType.NotNull}")
  @OneOf(enumClass = StreamType.class, message = "{stream.streamType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("stream_type")
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isEvent() {
    return StreamType.isEvent(getStreamType());
  }
}
