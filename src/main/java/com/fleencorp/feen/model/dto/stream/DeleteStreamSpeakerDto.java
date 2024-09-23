package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.util.FleenUtil;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
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

  @NotNull(message = "{speaker.speakers.NotNull}")
  @JsonProperty("speaker_ids")
  private Set<String> speakerIds;

  public Set<StreamSpeaker> toStreamSpeakers() {
    return speakerIds.stream()
      .filter(FleenUtil::isValidNumber)
      .map(Long::parseLong)
      .map(StreamSpeaker::of)
      .collect(Collectors.toSet());
  }


}
