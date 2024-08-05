package com.fleencorp.feen.configuration.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

/**
*
* @see <a href="https://velog.io/@sehyunny/a-first-look-at-htmx-and-how-it-compares-to-react">Exploring HTMX and comparing it to React</a>
* @see <a href="https://velog.io/@eunbinn/htmx-the-newest-old-way-to-make-web-apps">Htmx: The Newest Oldest Way to Create Web Apps</a>
* @swe <a href="https://velog.io/@leaftree/20240222-Spring-9">Thumbnail Creator & 20240223 Spring 9 - Mail (Send Function), File Upload</a>
* @see <a href="https://velog.io/@yeddoen/Spring-%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%A0%84%EC%86%A1%ED%95%98%EA%B8%B0">[Spring] Sending email</a>
*/
@Configuration
public class TemplateConfiguration {

  private final String mailTemplatesPath;

  public TemplateConfiguration(
      @Value("${email.message.templates.path}") final String mailTemplatesPath) {
    this.mailTemplatesPath = mailTemplatesPath;
  }

  /**
   * Configures and returns a Thymeleaf template resolver bean.
   *
   * <p>This method sets up the template resolver to load email templates from the specified path,
   * using the HTML template mode and UTF-8 character encoding.</p>
   *
   * @return the configured ITemplateResolver bean
   * @see <a href="https://velog.io/@juno0713/Spring-Redis-%EC%9D%B4%EB%A9%94%EC%9D%BC-%EC%9D%B8%EC%A6%9D-%EC%95%A0%ED%94%8C%EB%A6%AC%EC%BC%80%EC%9D%B4">
   *   Spring + Redis Email Authentication Application</a>
   * @see <a href="https://velog.io/@itoriginal/spring-info-email">[Spring] Spring Boot E-mail authentication</a>
   */
  @Bean
  public ITemplateResolver thymeleafTemplateResolver() {
    // Create a new template resolver for class loader-based template loading
    final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
    // Set the prefix for the email templates path
    templateResolver.setPrefix(mailTemplatesPath);
    // Set the suffix for the email template files
    templateResolver.setSuffix(".html");
    // Set the template mode to HTML
    templateResolver.setTemplateMode(TemplateMode.HTML);
    // Whether to cache the templates
    templateResolver.setCacheable(false);
    // Set the character encoding to UTF-8
    templateResolver.setCharacterEncoding("UTF-8");
    // Return the configured template resolver
    return templateResolver;
  }

  /**
   * Configures and returns a Thymeleaf template engine bean.
   *
   * <p>This method sets up the template engine with the previously configured
   * Thymeleaf template resolver.</p>
   *
   * @return the configured SpringTemplateEngine bean
   */
  @Bean
  public SpringTemplateEngine thymeleafTemplateEngine() {
    // Create a new instance of SpringTemplateEngine
    final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
    // Set the template resolver for the template engine
    templateEngine.setTemplateResolver(thymeleafTemplateResolver());
    // Return the configured template engine
    return templateEngine;
  }

}
