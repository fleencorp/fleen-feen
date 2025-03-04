package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class MarkAsStreamSpeakerResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "mark.as.stream.speaker";
  }

  public static MarkAsStreamSpeakerResponse of() {
    return new MarkAsStreamSpeakerResponse();
  }
}
