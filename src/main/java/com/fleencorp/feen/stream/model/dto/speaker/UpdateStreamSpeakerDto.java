package com.fleencorp.feen.stream.model.dto.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UpdateStreamSpeakerDto extends AddStreamSpeakerDto {

  @Valid
  @NotEmpty(message = "{speaker.speakers.NotEmpty}")
  @Size(max = 10, message = "{speaker.speakers.Size}")
  @JsonProperty("speakers")
  protected List<StreamSpeakerDto> speakers = new ArrayList<>();
}
