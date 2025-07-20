package com.fleencorp.feen.stream.model.response.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.model.info.attendee.IsASpeakerInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "message",
  "is_a_speaker_info"
})
public class MarkAsStreamSpeakerResponse extends LocalizedResponse {

  @JsonProperty("is_a_speaker_info")
  private IsASpeakerInfo isASpeakerInfo;

  @Override
  public String getMessageCode() {
    return "mark.as.stream.speaker";
  }

  public static MarkAsStreamSpeakerResponse of(final IsASpeakerInfo isASpeakerInfo) {
    return new MarkAsStreamSpeakerResponse(isASpeakerInfo);
  }
}
