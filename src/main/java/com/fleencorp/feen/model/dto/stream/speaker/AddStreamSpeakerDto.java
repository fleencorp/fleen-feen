package com.fleencorp.feen.model.dto.stream.speaker;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddStreamSpeakerDto {

  protected Set<StreamSpeakerDto> speakers;

  /**
   * Converts a list of StreamSpeakerDto objects to a set of StreamSpeaker objects associated with a given FleenStream.
   *
   * @param stream the FleenStream to which the speakers will be associated.
   * @return a Set of StreamSpeaker objects created from the provided speaker DTOs.
   */
  public Set<StreamSpeaker> toStreamSpeakers(final FleenStream stream) {
    final Set<StreamSpeaker> streamSpeakers = new HashSet<>();

    // Iterate through each StreamSpeakerDto in the speakers list
    for (final StreamSpeakerDto speakerDto : speakers) {
      // Convert the StreamSpeakerDto to a StreamSpeaker
      final StreamSpeaker streamSpeaker = speakerDto.toStreamSpeaker(stream);
      // Add it to the set
      streamSpeakers.add(streamSpeaker);
    }
    // Return the set of StreamSpeaker objects
    return streamSpeakers;
  }
}
