package com.fleencorp.feen.model.other;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MfaAuthenticatorSecurityInfo {

  private String qrCode;
  private String secret;

  public static MfaAuthenticatorSecurityInfo of(String qrCode, String secret) {
    return MfaAuthenticatorSecurityInfo.builder()
        .qrCode(qrCode)
        .secret(secret)
        .build();
  }
}
