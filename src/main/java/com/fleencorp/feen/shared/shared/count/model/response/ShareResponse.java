package com.fleencorp.feen.shared.shared.count.model.response;

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
public class ShareResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "share";
  }

  public static ShareResponse of() {
    return new ShareResponse();
  }
}
