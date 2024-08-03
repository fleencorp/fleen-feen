package com.fleencorp.feen.configuration.security.properties;

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
@ConfigurationProperties(prefix = "token.validity.duration.of")
@PropertySources({
  @PropertySource("classpath:properties/token.properties")
})
public class TokenProperties {

  private Long accessToken;
  private Long refreshToken;
  private Long resetPasswordToken;
}
