package com.fleencorp.feen.model.response.social.share;

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
public class ProcessShareContactRequestResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "process.share.contact.request";
  }

  public static ProcessShareContactRequestResponse of() {
    return new ProcessShareContactRequestResponse();
  }
}
