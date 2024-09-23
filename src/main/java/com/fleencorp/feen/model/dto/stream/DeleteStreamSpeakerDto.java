package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.util.FleenUtil;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStreamSpeakerDto {

  @Valid
  @NotNull(message = "{speaker.speakers.NotNull}")
  @JsonProperty("speakers")
  private Set<DeleteSpeakersDto> speakerIds;

  /**
   * Converts a set of speaker IDs into a set of {@link StreamSpeaker} objects.
   * The method filters out any invalid IDs before converting the valid ones to {@link StreamSpeaker} objects.
   *
   * @return a set of {@link StreamSpeaker} objects created from the valid speaker IDs.
   */
  public Set<StreamSpeaker> toStreamSpeakers() {
    return speakerIds.stream()
      .map(DeleteSpeakersDto::getSpeakerId)
      .filter(FleenUtil::isValidNumber)
      .map(Long::parseLong)
      .map(StreamSpeaker::of)
      .collect(Collectors.toSet());
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeleteSpeakersDto {

    @JsonProperty("speaker_id")
    private String speakerId;
  }


}
