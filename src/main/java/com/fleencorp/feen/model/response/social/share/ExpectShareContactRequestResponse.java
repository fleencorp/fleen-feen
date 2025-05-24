package com.fleencorp.feen.model.response.social.share;

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
public class ExpectShareContactRequestResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "expect.share.contact.request";
  }

  public static ExpectShareContactRequestResponse of() {
    return new ExpectShareContactRequestResponse();
  }
}
