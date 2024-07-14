package com.fleencorp.feen.config.security.properties;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "token.mfa")
@PropertySources({
  @PropertySource("classpath:token.properties")
})
public class MfaProperties {

  private String secretIssuer;
  private String secretLabel;
}
