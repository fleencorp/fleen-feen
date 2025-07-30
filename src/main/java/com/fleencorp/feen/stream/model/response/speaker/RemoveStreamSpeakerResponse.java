package com.fleencorp.feen.stream.model.response.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
public class RemoveStreamSpeakerResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "remove.stream.speaker";
  }

  public static RemoveStreamSpeakerResponse of() {
    return new RemoveStreamSpeakerResponse();
  }
}
