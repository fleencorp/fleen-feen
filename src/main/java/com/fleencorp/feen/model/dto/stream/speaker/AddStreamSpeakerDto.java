package com.fleencorp.feen.model.dto.stream.speaker;

import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public abstract class AddStreamSpeakerDto {

  abstract List<StreamSpeakerDto> getSpeakers();

  /**
   * Converts a list of StreamSpeakerDto objects to a set of StreamSpeaker objects associated with a given FleenStream.
   *
   * @param stream the FleenStream to which the speakers will be associated.
   * @return a Set of StreamSpeaker objects created from the provided speaker DTOs.
   */
  public Set<StreamSpeaker> toStreamSpeakers(final FleenStream stream) {
    final List<StreamSpeaker> streamSpeakers = new ArrayList<>();

    System.out.println("The total number of speakers is " + getSpeakers().size());
    // Iterate through each StreamSpeakerDto in the speakers list
    for (final StreamSpeakerDto speakerDto : getSpeakers()) {
      // Convert the StreamSpeakerDto to a StreamSpeaker
      final StreamSpeaker streamSpeaker = speakerDto.toStreamSpeaker(stream);
      // Add it to the set
      streamSpeakers.add(streamSpeaker);
    }
    // Return the set of StreamSpeaker objects
    return new HashSet<>(streamSpeakers);
  }
}
