package com.fleencorp.feen.stream.model.dto.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToSentenceCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.*;
import com.fleencorp.feen.common.validator.StreamDuration;
import com.fleencorp.feen.common.validator.TimezoneValid;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fleencorp.feen.stream.constant.core.StreamCreationType.SCHEDULED;
import static com.fleencorp.feen.stream.constant.core.StreamStatus.ACTIVE;
import static java.lang.Boolean.parseBoolean;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@StreamDuration
@DateRange(start = "startDateTime", end = "endDateTime")
public class CreateStreamDto {

  @NotBlank(message = "{stream.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{stream.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  protected String title;

  @NotBlank(message = "{stream.description.NotBlank}")
  @Size(max = 3000, message = "{stream.description.Size}")
  @ToSentenceCase
  @JsonProperty("description")
  protected String description;

  @NotBlank(message = "{stream.tags.NotBlank}")
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
  @OneOf(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  protected String visibility;

  @NotNull(message = "{stream.forKids.NotNull}")
  @ValidBoolean
  @JsonProperty("is_for_kids")
  protected String forKids;

  @Size(min = 3, max = 100, message = "{stream.organizerAliasOrDisplayName.Size}")
  @JsonProperty("organizer_alias_or_display_name")
  private String organizerAliasOrDisplayName;

  /**
   * Retrieves the organizer's alias or display name. If it is not set or is blank, returns a default value.
   *
   * @param defaultOrganizerAlias The fallback alias to return if the organizer's alias or display name is not provided.
   * @return The organizer's alias or display name, or the provided default value if not available.
   */
  @JsonIgnore
  public String getOrganizerAlias(final String defaultOrganizerAlias) {
    // Check if organizerAliasOrDisplayName is set and not blank
    if (nonNull(organizerAliasOrDisplayName) && !organizerAliasOrDisplayName.trim().isBlank()) {
      return organizerAliasOrDisplayName;
    } else {
      return defaultOrganizerAlias;
    }
  }

  public StreamVisibility getActualVisibility() {
    return StreamVisibility.of(visibility);
  }

  public LocalDateTime getStartDateTime() {
    return LocalDateTime.parse(startDateTime);
  }

  public LocalDateTime getEndDateTime() {
    return LocalDateTime.parse(endDateTime);
  }

  public FleenStream toFleenStream() {
    final FleenStream stream = new FleenStream();
    stream.setTags(tags);
    stream.setTitle(title);
    stream.setLocation(location);
    stream.setTimezone(timezone);
    stream.setDescription(description);

    stream.setScheduledStartDate(getStartDateTime());
    stream.setScheduledEndDate(getEndDateTime());
    stream.setStreamVisibility(getActualVisibility());

    stream.setStreamStatus(ACTIVE);
    stream.setStreamCreationType(SCHEDULED);

    stream.setForKids(parseBoolean(forKids));
    stream.setDeleted(false);
    return stream;
  }

}
