package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.converter.common.ToLowerCase;
import com.fleencorp.feen.converter.common.ToTitleCase;
import com.fleencorp.feen.converter.common.ToUpperCase;
import com.fleencorp.feen.validator.TimezoneValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@DateRange(start = "startDateTime", end = "endDateTime")
public class CreateStreamDto {

  @NotBlank(message = "{stream.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{stream.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  protected String title;

  @NotBlank(message = "{stream.description.NotBlank}")
  @Size(max = 3000, message = "{stream.description.Size}")
  @JsonProperty("description")
  protected String description;

  @NotEmpty(message = "{stream.tags.NotBlank}")
  @Size(min = 1, max = 300, message = "{stream.tags.Size}")
  @ToLowerCase
  @JsonProperty("tags")
  protected String tags;

  @NotBlank(message = "{stream.location.NotBlank}")
  @Size(max = 50, message = "{stream.location.Size}")
  @ToTitleCase
  @JsonProperty("location")
  protected String location;

  @NotBlank(message = "{stream.timezone.NotBlank}")
  @Size(max = 50, message = "{stream.timezone.Size}")
  @TimezoneValid
  @ToLowerCase
  @JsonProperty("timezone")
  protected String timezone;

  @NotNull(message = "{stream.startDateTime.NotNull}")
  @FutureDate
  @DateTimeValid
  @JsonProperty("start_date_time")
  protected LocalDateTime startDateTime;

  @NotNull(message = "{stream.endDateTime.NotNull}")
  @FutureDate
  @DateTimeValid
  @JsonProperty("end_date_time")
  protected LocalDateTime endDateTime;

  @NotNull(message = "{stream.visibility.NotNull}")
  @ValidEnum(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  protected String visibility;

  @NotNull(message = "{stream.type.NotNull}")
  @ValidEnum(enumClass = StreamType.class, message = "{stream.type.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("type")
  protected String type;

  @NotNull(message = "{stream.forKids.NotNull}")
  @ValidBoolean
  @JsonProperty("is_for_kids")
  protected String isForKids;

  public StreamType getActualType() {
    return StreamType.of(type);
  }

  public StreamVisibility getActualVisibility() {
    return StreamVisibility.of(visibility);
  }

}
