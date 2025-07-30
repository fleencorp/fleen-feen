package com.fleencorp.feen.stream.model.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fleencorp.feen.common.constant.base.SimpleConstant.NOT_AVAILABLE;
import static com.fleencorp.feen.stream.constant.core.StreamCreationType.INSTANT;
import static com.fleencorp.feen.stream.constant.core.StreamSource.GOOGLE_MEET;
import static com.fleencorp.feen.stream.constant.core.StreamStatus.ACTIVE;
import static com.fleencorp.feen.stream.constant.core.StreamVisibility.PUBLIC;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateInstantEventDto {

  @NotBlank(message = "{stream.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{stream.title.Size}")
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

  public FleenStream toFleenStream(final Member member) {
    final FleenStream stream = new FleenStream();
    stream.setTitle(title);
    stream.setDescription(description);
    stream.setTags(tags);
    stream.setLocation(location);
    stream.setTimezone(NOT_AVAILABLE);
    stream.setMember(member);
    stream.setScheduledStartDate(LocalDateTime.now());
    stream.setScheduledEndDate(LocalDateTime.now());
    stream.setStreamSource(GOOGLE_MEET);
    stream.setStreamVisibility(PUBLIC);
    stream.setStreamCreationType(INSTANT);
    stream.setStreamStatus(ACTIVE);
    stream.setForKids(true);
    stream.setDeleted(false);

    return stream;
  }
}
