package com.fleencorp.feen.stream.model.info.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "speaker_count",
  "speaker_count_text"
})
public class SpeakerCountInfo {

  @JsonProperty("speaker_count")
  private Integer speakerCount;

  @JsonProperty("speaker_count_text")
  private String speakerCountText;

  public static SpeakerCountInfo of(final Integer speakerCount, final String speakerCountText) {
    return new SpeakerCountInfo(speakerCount, speakerCountText);
  }
}
