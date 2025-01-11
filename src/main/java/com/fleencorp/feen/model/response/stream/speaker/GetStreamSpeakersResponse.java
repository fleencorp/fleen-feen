package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "speakers"
})
public class GetStreamSpeakersResponse extends ApiResponse {

  @JsonProperty("speakers")
  private Set<StreamSpeakerResponse> speakers;

  @Override
  public String getMessageCode() {
    return "get.stream.speakers";
  }

  public static GetStreamSpeakersResponse of(final Set<StreamSpeakerResponse> speakers) {
    return new GetStreamSpeakersResponse(speakers);
  }
}
