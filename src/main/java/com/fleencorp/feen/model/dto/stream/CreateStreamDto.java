package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.constant.stream.StreamSource;
import com.fleencorp.feen.constant.stream.StreamVisibility;
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

import static java.util.Objects.nonNull;

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
  @JsonProperty("timezone")
  protected String timezone;

  @NotNull(message = "{stream.startDateTime.NotNull}")
  @DateTimeValid
  @FutureDate
  @JsonProperty("start_date_time")
  protected String startDateTime;

  @NotNull(message = "{stream.endDateTime.NotNull}")
  @DateTimeValid
  @FutureDate
  @JsonProperty("end_date_time")
  protected String endDateTime;

  @NotNull(message = "{stream.visibility.NotNull}")
  @ValidEnum(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  protected String visibility;

  @NotNull(message = "{stream.type.NotNull}")
  @ValidEnum(enumClass = StreamSource.class, message = "{stream.type.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("source")
  protected String source;

  @NotNull(message = "{stream.forKids.NotNull}")
  @ValidBoolean
  @JsonProperty("is_for_kids")
  protected String isForKids;

  @Size(min = 3, max = 100, message = "{event.organizerAliasOrDisplayName.Size}")
  @JsonProperty("organizer_alias_or_display_name")
  private String organizerAliasOrDisplayName;

  @JsonIgnore
  public String getOrganizerAlias(final String defaultOrganizerAlias) {
    if (nonNull(organizerAliasOrDisplayName) && !organizerAliasOrDisplayName.trim().isBlank()) {
      return organizerAliasOrDisplayName;
    }
    else {
      return defaultOrganizerAlias;
    }
  }

  public StreamSource getActualSource() {
    return StreamSource.of(source);
  }

  public StreamVisibility getActualVisibility() {
    return StreamVisibility.of(visibility);
  }

  public LocalDateTime getActualStartDateTime() {
    return LocalDateTime.parse(startDateTime);
  }

  public LocalDateTime getActualEndDateTime() {
    return LocalDateTime.parse(endDateTime);
  }

}
