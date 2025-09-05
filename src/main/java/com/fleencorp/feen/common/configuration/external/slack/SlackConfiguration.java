package com.fleencorp.feen.common.configuration.external.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SlackConfiguration {

  private final SlackProperties slackProperties;

  public SlackConfiguration(final SlackProperties slackProperties) {
    this.slackProperties = slackProperties;
  }

  @Bean
  public Slack slack() {
    return Slack.getInstance();
  }

  /**
   * Creates and returns a Slack MethodsClient instance.
   *
   * <p>This method initializes a new MethodsClient using the Slack token specified in the slack properties.</p>
   *
   * @return a MethodsClient instance configured with the report token
   *
   * @see <a href="https://velog.io/@devand/%EC%8A%AC%EB%9E%99-Web-API-%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%B4%EB%B3%B4%EC%9E%90-1">
   *   Let's use Slack Web API (1)</a>
   */
  @Bean
  public MethodsClient methodsClient() {
    return slack().methods(slackProperties.getReportToken());
  }
}
