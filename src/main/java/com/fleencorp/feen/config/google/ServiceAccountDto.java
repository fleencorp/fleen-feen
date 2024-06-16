package com.fleencorp.feen.config.google;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * The {@link ServiceAccountDto} class provides a static factory method, {@code fromServiceAccountProperties},
 * which creates a new instance of {@code ServiceAccountDto} based on the provided {@link ServiceAccountProperties}.
 * This method copies the properties from the {@link ServiceAccountProperties} object to the corresponding
 * fields of the {@code ServiceAccountDto} object using the Lombok-generated builder.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ServiceAccountDto extends ServiceAccountProperties {

  /**
   * Static factory method to create a new {@code ServiceAccountDto} instance
   * based on the provided {@link ServiceAccountProperties}.
   *
   * @param serviceAccountProperties the {@link ServiceAccountProperties} object
   *                                 from which to create the {@code ServiceAccountDto}
   * @return a new instance of {@code ServiceAccountDto} populated with properties from
   *         the {@link ServiceAccountProperties} object
   */
  public static ServiceAccountDto fromServiceAccountProperties(ServiceAccountProperties serviceAccountProperties) {
    return ServiceAccountDto
      .builder()
      .type(serviceAccountProperties.getType())
      .projectId(serviceAccountProperties.getProjectId())
      .privateKeyId(serviceAccountProperties.getPrivateKeyId())
      .privateKey(serviceAccountProperties.getPrivateKey())
      .clientEmail(serviceAccountProperties.getClientEmail())
      .clientId(serviceAccountProperties.getClientId())
      .authUri(serviceAccountProperties.getAuthUri())
      .tokenUri(serviceAccountProperties.getTokenUri())
      .authProviderX509CertUrl(serviceAccountProperties.getAuthProviderX509CertUrl())
      .clientX509CertUrl(serviceAccountProperties.getClientX509CertUrl())
      .universeDomain(serviceAccountProperties.getUniverseDomain())
      .build();
  }
}
