package com.fleencorp.feen.model.response.share.share;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendShareContactRequestResponse {

  @Builder.Default
  private String message = "Send Share contact request successful.";

  public static SendShareContactRequestResponse of() {
    return new SendShareContactRequestResponse();
  }
}
