package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.*;

import java.util.Set;

@Builder
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
    return GetStreamSpeakersResponse.builder()
      .speakers(speakers)
      .build();
  }
}
