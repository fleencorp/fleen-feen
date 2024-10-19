package com.fleencorp.feen.configuration.external.slack;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "slack")
@PropertySources({
  @PropertySource("classpath:properties/slack.properties")
})
public class SlackProperties {

  private String reportToken;
  private String channelErrorReportId;
  private String channelWarnReportId;
  private String channelInfoReportId;

  private String channelGoogleCalendarReportId;
  private String channelGoogleChatReportId;
  private String channelGoogleOauth2ReportId;
  private String channelYoutubeReportId;
  private String channelVerificationReportId;
  private String channelGeneralReportId;
}
