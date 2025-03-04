package com.fleencorp.feen.model.dto.stream.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsStreamSpeakerDto extends AddStreamSpeakerDto {

  @Valid
  @NotNull(message = "{speaker.speakers.NotNull}")
  @Size(max = 1, message = "{speaker.speakers.Size}")
  @JsonProperty("speakers")
  protected Set<StreamSpeakerDto> speakers;
}
