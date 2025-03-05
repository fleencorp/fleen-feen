package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "speakers"
})
public class UpdateStreamSpeakerResponse extends ApiResponse {

  @JsonProperty("speakers")
  private List<StreamSpeakerResponse> speakers;

  @Override
  public String getMessageCode() {
    return "update.stream.speaker";
  }

  public static UpdateStreamSpeakerResponse of(final List<StreamSpeakerResponse> speakers) {
    return new UpdateStreamSpeakerResponse(speakers);
  }
}
