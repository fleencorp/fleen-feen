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
public class CancelShareContactRequestResponse {

  @Builder.Default
  private String message = "Cancel share contact request successful.";

  public static CancelShareContactRequestResponse of() {
    return new CancelShareContactRequestResponse();
  }
}
