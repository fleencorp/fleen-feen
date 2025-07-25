package com.fleencorp.feen.mfa.model.info;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MfaAuthenticatorSecurityInfo {

  private String qrCode;
  private String secret;

  public static MfaAuthenticatorSecurityInfo of(final String qrCode, final String secret) {
    return MfaAuthenticatorSecurityInfo.builder()
        .qrCode(qrCode)
        .secret(secret)
        .build();
  }
}
