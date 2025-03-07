package com.fleencorp.feen.model.dto.stream.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.util.FleenUtil;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveStreamSpeakerDto {

  @Valid
  @NotNull(message = "{speaker.speakers.NotNull}")
  @Size(max = 10, message = "{speaker.speakers.Size}")
  @JsonProperty("speakers")
  private List<DeleteSpeakerDto> speakers;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeleteSpeakerDto {

    @JsonProperty("speaker_id")
    private String speakerId;
  }

  /**
   * Converts a set of speaker IDs into a set of {@link StreamSpeaker} objects.
   * The method filters out any invalid IDs before converting the valid ones to {@link StreamSpeaker} objects.
   *
   * @return a set of {@link StreamSpeaker} objects created from the valid speaker IDs.
   */
  public Set<StreamSpeaker> toStreamSpeakers() {
    return speakers.stream()
      .map(DeleteSpeakerDto::getSpeakerId)
      .filter(FleenUtil::isValidNumber)
      .map(Long::parseLong)
      .map(StreamSpeaker::of)
      .collect(Collectors.toSet());
  }
}
