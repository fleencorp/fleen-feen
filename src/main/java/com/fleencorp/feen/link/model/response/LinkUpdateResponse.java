package com.fleencorp.feen.link.model.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
  "message"
})
public class LinkUpdateResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.link";
  }

  public static LinkUpdateResponse of() {
    return new LinkUpdateResponse();
  }
}
