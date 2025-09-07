package com.fleencorp.feen.link.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class LinkDeleteResponse extends LocalizedResponse {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "delete.link";
  }

  public static LinkDeleteResponse of() {
    return new LinkDeleteResponse();
  }
}
