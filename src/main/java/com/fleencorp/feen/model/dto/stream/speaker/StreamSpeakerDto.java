package com.fleencorp.feen.model.dto.stream.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamSpeakerDto {

  @IsNumber
  @JsonProperty("speakerId")
  private String speakerId;

  @IsNumber
  @JsonProperty("attendee_id")
  private String attendeeId;

  @NotBlank(message = "{speaker.fullName.NotBlank}")
  @Size(min = 10, max = 500, message = "{speaker.fullName.Size}")
  @ToTitleCase
  @JsonProperty("full_name")
  private String fullName;

  @NotBlank(message = "{speaker.title.NotBlank}")
  @Size(min = 5, max = 500, message = "{speaker.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  private String title;

  @Size(max = 3000, message = "{speaker.description.Size}")
  @JsonProperty("description")
  private String description;

  /**
   * Retrieves the stream speaker ID by converting it to a Long.
   *
   * @return the stream speaker ID as a {@link Long}, or null if the ID is not set.
   */
  private Long getStreamSpeakerId() {
    return nonNull(speakerId) ? Long.parseLong(speakerId) : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StreamSpeakerDto that = (StreamSpeakerDto) o;
    return Objects.equals(attendeeId, that.attendeeId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(attendeeId);
  }

  public StreamSpeaker toStreamSpeaker(final FleenStream stream) {
    final StreamSpeaker streamSpeaker = toStreamSpeaker();
    streamSpeaker.setStream(stream);

    return streamSpeaker;
  }

  public StreamSpeaker toStreamSpeaker() {
    final StreamSpeaker speaker = new StreamSpeaker();
    speaker.setSpeakerId(getStreamSpeakerId());
    speaker.setFullName(fullName);
    speaker.setTitle(title);
    speaker.setDescription(description);
    speaker.setAttendee(StreamAttendee.of(attendeeId));

    return speaker;
  }
}
