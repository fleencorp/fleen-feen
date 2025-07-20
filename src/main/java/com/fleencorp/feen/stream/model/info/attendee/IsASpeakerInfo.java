package com.fleencorp.feen.stream.model.info.attendee;

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
  "is_a_speaker",
  "is_a_speaker_text",
  "is_a_speaker_text_2"
})
public class IsASpeakerInfo {

  @JsonProperty("is_a_speaker")
  private Boolean isASpeaker;

  @JsonProperty("is_a_speaker_text")
  private String isASpeakerText;

  @JsonProperty("is_a_speaker_text_2")
  private String isASpeakerText2;

  public static IsASpeakerInfo of(final Boolean isASpeaker, final String isASpeakerText, final String isASpeakerText2) {
    return new IsASpeakerInfo(isASpeaker, isASpeakerText, isASpeakerText2);
  }

  public static IsASpeakerInfo of() {
    return new IsASpeakerInfo();
  }
}
