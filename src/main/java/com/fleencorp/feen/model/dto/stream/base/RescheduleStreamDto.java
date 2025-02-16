package com.fleencorp.feen.model.dto.stream.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.DateTimeValid;
import com.fleencorp.base.validator.FutureDate;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleStreamDto {

  @NotBlank(message = "{stream.timezone.NotBlank}")
  @Size(max = 50, message = "{stream.timezone.Size}")
  @TimezoneValid
  @JsonProperty("timezone")
  private String timezone;

  @NotNull(message = "{stream.startDateTime.NotNull}")
  @DateTimeValid
  @FutureDate
  @JsonProperty("start_date_time")
  private String startDateTime;

  @NotNull(message = "{stream.endDateTime.NotNull}")
  @DateTimeValid
  @FutureDate
  @JsonProperty("end_date_time")
  private String endDateTime;

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

  public LocalDateTime getStartDateTime() {
    return LocalDateTime.parse(startDateTime);
  }

  public LocalDateTime getEndDateTime() {
    return LocalDateTime.parse(endDateTime);
  }
}
