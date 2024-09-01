package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.DateTimeValid;
import com.fleencorp.base.validator.FutureDate;
import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
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

  public LocalDateTime getActualStartDateTime() {
    return LocalDateTime.parse(startDateTime);
  }

  public LocalDateTime getActualEndDateTime() {
    return LocalDateTime.parse(endDateTime);
  }
}
