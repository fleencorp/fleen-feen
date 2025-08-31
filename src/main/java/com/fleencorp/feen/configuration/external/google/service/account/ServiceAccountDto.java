package com.fleencorp.feen.configuration.external.google.service.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* The {@link ServiceAccountDto} class provides a static factory method, {@code fromServiceAccountProperties},
* which creates a new instance of {@code ServiceAccountDto} based on the provided {@link ServiceAccountProperties}.
* This method copies the properties from the {@link ServiceAccountProperties} object to the corresponding
* fields of the {@code ServiceAccountDto} object using the Lombok-generated builder.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
@Setter
@NoArgsConstructor
public class ServiceAccountDto extends ServiceAccountProperties {

  /**
  * Static factory method to create a new {@code ServiceAccountDto} instance
  * based on the provided {@link ServiceAccountProperties}.
  *
  * @param properties the {@link ServiceAccountProperties} object
  *                                 from which to create the {@code ServiceAccountDto}
  * @return a new instance of {@code ServiceAccountDto} populated with properties from
  *         the {@link ServiceAccountProperties} object
  */
  public static ServiceAccountDto fromServiceAccountProperties(final ServiceAccountProperties properties) {
    final ServiceAccountDto dto = new ServiceAccountDto();
    dto.setType(properties.getType());
    dto.setAuthUri(properties.getAuthUri());
    dto.setTokenUri(properties.getTokenUri());
    dto.setClientId(properties.getClientId());
    dto.setProjectId(properties.getProjectId());
    dto.setPrivateKey(properties.getPrivateKey());
    dto.setPrivateKeyId(properties.getPrivateKeyId());
    dto.setClientEmail(properties.getClientEmail());
    dto.setUniverseDomain(properties.getUniverseDomain());
    dto.setClientX509CertUrl(properties.getClientX509CertUrl());
    dto.setAuthProviderX509CertUrl(properties.getAuthProviderX509CertUrl());

    return dto;
  }
}
