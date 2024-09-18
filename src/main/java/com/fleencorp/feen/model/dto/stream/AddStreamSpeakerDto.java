package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddStreamSpeakerDto {

  @Valid
  @NotNull(message = "{speaker.speakers.NotNull}")
  @Size(max = 5, message = "{speaker.speakers.Size}")
  @JsonProperty("speakers")
  private Set<StreamSpeakerDto> speakers;

  /**
   * Converts a collection of {@link StreamSpeakerDto} objects to a set of {@link StreamSpeaker} entities.
   *
   * <p>This method iterates over the collection of {@code StreamSpeakerDto} objects, converts each {@code StreamSpeakerDto}
   * to a {@code StreamSpeaker} entity using its {@code toStreamSpeaker} method, and adds the resulting entities to a {@code HashSet}.
   * The resulting set of {@code StreamSpeaker} entities is then returned.</p>
   *
   * @return a {@link Set} of {@link StreamSpeaker} entities corresponding to the provided {@link StreamSpeakerDto} objects
   */
  public Set<StreamSpeaker> toStreamSpeakers(final FleenStream fleenStream) {
    final Set<StreamSpeaker> streamSpeakers = new HashSet<>();

    for (final StreamSpeakerDto speakerDto : speakers) {
      streamSpeakers.add(speakerDto.toStreamSpeaker(fleenStream));
    }
    return streamSpeakers;
  }
}
