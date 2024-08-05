package com.fleencorp.feen.configuration.message;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail")
public class EmailMessageProperties {

  private String originEmailAddress;
  private String supportEmailAddress;
  private String noReplyEmailAddress;
}
