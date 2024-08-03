package com.fleencorp.feen.model.response.share.share;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class ProcessShareContactRequestResponse {

  @Builder.Default
  private String message = "Process Share Contact Request successful.";

  public static ProcessShareContactRequestResponse of() {
    return new ProcessShareContactRequestResponse();
  }
}
