package com.fleencorp.feen.service.impl.message;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * TemplateProcessor handles processing of Thymeleaf and SMS message templates using a configured SpringTemplateEngine
 * and Java {@link java.text.MessageFormat}.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class TemplateProcessor {

  private final SpringTemplateEngine templateEngine;

  /**
   * Constructs a TemplateProcessor with the provided SpringTemplateEngine.
   *
   * @param templateEngine the SpringTemplateEngine used for processing templates
   */
  public TemplateProcessor(final SpringTemplateEngine templateEngine) {
    this.templateEngine = templateEngine;
  }

  /**
   * Processes a Thymeleaf template with the provided template name and variables.
   *
   * <p>This method creates a Thymeleaf context with the given template variables,
   * then uses the {@link SpringTemplateEngine} to process the template and
   * return the processed output as a String.</p>
   *
   * @param templateName       the name of the Thymeleaf template to process
   * @param templateVariables  a map of variables to be used in the template processing
   * @return                   the processed template output as a String
   */
  public String processTemplate(final String templateName, final Map<String, Object> templateVariables) {
    final Context thymeleafContext = new Context();
    thymeleafContext.setVariables(templateVariables);
    return templateEngine.process(templateName, thymeleafContext);
  }

  /**
   * Processes an SMS template body by substituting placeholders with actual values from template variables.
   *
   * @param templateBody      the SMS template body containing placeholders
   * @param templateVariables a map of key-value pairs where keys represent placeholders in the template body
   *                          and values are the actual values to substitute
   * @return the processed SMS template body with placeholders replaced by actual values
   */
  public String processTemplateSms(final String templateBody, final Map<String, Object> templateVariables) {
    final StringSubstitutor substitutor = new StringSubstitutor(templateVariables);
    return substitutor.replace(templateBody);
  }

}
