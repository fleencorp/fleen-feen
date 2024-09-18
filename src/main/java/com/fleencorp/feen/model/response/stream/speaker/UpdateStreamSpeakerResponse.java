package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class UpdateStreamSpeakerResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "update.stream.speaker";
  }

  public static UpdateStreamSpeakerResponse of() {
    return new UpdateStreamSpeakerResponse();
  }
}
