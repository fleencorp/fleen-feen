package com.fleencorp.feen.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.converter.common.ToLowerCase;
import com.fleencorp.feen.converter.common.ToTitleCase;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

import static com.fleencorp.feen.constant.base.SimpleConstant.NOT_AVAILABLE;
import static com.fleencorp.feen.constant.stream.StreamCreationType.INSTANT;
import static com.fleencorp.feen.constant.stream.StreamStatus.ACTIVE;
import static com.fleencorp.feen.constant.stream.StreamType.GOOGLE_MEET;
import static com.fleencorp.feen.constant.stream.StreamVisibility.PUBLIC;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstantCalendarEventDto {

  @NotBlank(message = "{event.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{event.title.Size}")
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{stream.description.NotBlank}")
  @Size(max = 3000, message = "{stream.description.Size}")
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

  public FleenStream toFleenStream() {
    return FleenStream.builder()
            .title(title)
            .description(description)
            .tags(tags)
            .location(location)
            .timezone(NOT_AVAILABLE)
            .scheduledStartDate(LocalDateTime.now())
            .scheduledEndDate(LocalDateTime.now())
            .streamVisibility(PUBLIC)
            .streamType(GOOGLE_MEET)
            .streamCreationType(INSTANT)
            .streamStatus(ACTIVE)
            .forKids(true)
            .build();
  }
}
