package com.fleencorp.feen.configuration.external.google.service.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
* <p>Configuration class representing service account properties used for Google authentication.
* This class is annotated with various Lombok annotations for generating boilerplate code.
* It is also annotated with Spring's configuration-related annotations for property binding.
* </p><br/>
*
* <p>
* The properties are loaded from the "google-service-account.properties" file located in the classpath,
* with the prefix "sa". The values are then bound to the corresponding fields of this class using
* the {@link ConfigurationProperties} annotation.
* </p><br/>
*
*
* <p>
* The fields in this class represent the various properties required for Google service account authentication.
* Each field is annotated with {@link NotBlank} to ensure that the corresponding property value is not empty or blank.
* Additionally, each field is annotated with {@link JsonProperty} to specify the name of the property in JSON format.
* </p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "sa")
@PropertySources({
  @PropertySource("classpath:google-service-account.properties")
})
public class ServiceAccountProperties {

  @NotBlank
  @JsonProperty("type")
  private String type;

  @NotBlank
  @JsonProperty("project_id")
  private String projectId;

  @NotBlank
  @JsonProperty("private_key_id")
  private String privateKeyId;

  @NotBlank
  @JsonProperty("private_key")
  private String privateKey;

  @NotBlank
  @JsonProperty("client_email")
  private String clientEmail;

  @NotBlank
  @JsonProperty("client_id")
  private String clientId;

  @NotBlank
  @JsonProperty("auth_uri")
  private String authUri;

  @NotBlank
  @JsonProperty("token_uri")
  private String tokenUri;

  @NotBlank
  @JsonProperty("auth_provider_x509_cert_url")
  private String authProviderX509CertUrl;

  @NotBlank
  @JsonProperty("client_x509_cert_url")
  private String clientX509CertUrl;

  @NotBlank
  @JsonProperty("universe_domain")
  private String universeDomain;
}
