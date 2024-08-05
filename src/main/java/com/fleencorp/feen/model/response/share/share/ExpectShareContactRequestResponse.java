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
public class ExpectShareContactRequestResponse {

  @Builder.Default
  private String message = "Expect share contact request successful.";

  public static ExpectShareContactRequestResponse of() {
    return new ExpectShareContactRequestResponse();
  }
}
