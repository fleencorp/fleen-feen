package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.DateTimeValid;
import com.fleencorp.base.validator.FutureDate;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
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
public class CreateStreamDto {

  @NotBlank(message = "{stream.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{stream.title.Size}")
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{stream.description.NotBlank}")
  @Size(max = 1000, message = "{stream.description.Size}")
  @JsonProperty("description")
  private String description;

  @NotBlank(message = "{stream.location.NotBlank}")
  @Size(max = 50, message = "{stream.location.Size}")
  @JsonProperty("location")
  private String location;

  @NotBlank(message = "{stream.timezone.NotBlank}")
  @Size(max = 50, message = "{stream.timezone.Size}")
  @JsonProperty("timezone")
  private String timezone;

  @NotNull(message = "{stream.startDateTime.NotNull}")
  @FutureDate
  @DateTimeValid
  @JsonProperty("start_date_time")
  private LocalDateTime startDateTime;

  @NotNull(message = "{stream.endDateTime.NotNull}")
  @FutureDate
  @DateTimeValid
  @JsonProperty("end_date_time")
  private LocalDateTime endDateTime;

  @NotNull(message = "{stream.visibility.NotNull}")
  @ValidEnum(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}")
  @JsonProperty("visibility")
  private String visibility;

  @NotNull(message = "{stream.type.NotNull}")
  @ValidEnum(enumClass = StreamType.class, message = "{stream.type.Type}")
  @JsonProperty("type")
  private String type;
}
