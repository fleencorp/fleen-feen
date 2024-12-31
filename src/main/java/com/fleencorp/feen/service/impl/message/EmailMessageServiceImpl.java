package com.fleencorp.feen.service.impl.message;

import com.fleencorp.feen.configuration.message.EmailMessageProperties;
import com.fleencorp.feen.exception.base.UnableToCompleteOperationException;
import com.fleencorp.feen.model.message.EmailMessage;
import com.fleencorp.feen.service.message.EmailMessageService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import static com.fleencorp.feen.constant.message.MessageTemplateField.LOGO;
import static com.fleencorp.feen.util.LoggingUtil.logIfEnabled;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

/**
 * EmailMessageServiceImpl implements EmailMessageService for sending email messages.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class EmailMessageServiceImpl implements EmailMessageService {

  private final Resource logoFile;
  private final EmailMessageProperties emailMessageProperties;
  private final JavaMailSender mailSender;

  /**
   * Constructs an EmailMessageServiceImpl with necessary dependencies.
   *
   * @param logoFile               the logo file resource for embedding in emails
   * @param emailMessageProperties configuration properties for email messages
   * @param javaMailSender         the JavaMailSender used for sending emails
   */
  public EmailMessageServiceImpl(
      @Value("${templates.logo-path}") final Resource logoFile,
      final EmailMessageProperties emailMessageProperties,
      final JavaMailSender javaMailSender) {
    this.logoFile = logoFile;
    this.emailMessageProperties = emailMessageProperties;
    this.mailSender = javaMailSender;
  }

  /**
   * Retrieves the origin email address from the email message properties.
   *
   * <p>This method fetches the email address configured as the origin address
   * for sending emails from the application's email properties.</p>
   *
   * @return the origin email address as a String
   */
  protected String getOriginEmailAddress() {
    return emailMessageProperties.getOriginEmailAddress();
  }

  /**
   * Sets the email body content in the MimeMessageHelper.
   *
   * <p>This method sets the plain text and/or HTML text of the email body based on
   * the provided EmailDetails object. It prioritizes both texts if available,
   * otherwise it sets the available text format.</p>
   *
   * @param helper  the MimeMessageHelper used to configure the email
   * @param details the EmailDetails object containing the email body content
   * @throws UnableToCompleteOperationException if a MessagingException occurs while setting the text
   */
  protected void setEmailBody(final MimeMessageHelper helper, final EmailMessage details) {
    try {
      // Check if both plain text and HTML text are available
      if (nonNull(details.getPlainText()) && nonNull(details.getHtmlText())) {
        // Set both plain text and HTML text
        helper.setText(details.getPlainText(), details.getHtmlText());
      } else if (nonNull(details.getHtmlText())) {
        // Set only HTML text if plain text is not available
        helper.setText(details.getHtmlText(), true);
      } else if (nonNull(details.getPlainText())) {
        // Set only plain text if HTML text is not available
        helper.setText(details.getPlainText());
      }
    } catch (final MessagingException ex) {
      // Log the exception message and stack trace
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      // Throw custom exception if unable to set the email body
      throw new UnableToCompleteOperationException();
    }
  }

  /**
   * Initializes a MimeMessageHelper with the provided MimeMessage and EmailDetails.
   *
   * <p>This method configures the MimeMessageHelper with the sender's email, recipient's email,
   * subject, and email body content. It uses mixed-related multipart mode and UTF-8 encoding.</p>
   *
   * @param message the MimeMessage to be configured
   * @param details the EmailDetails object containing the email configuration details
   * @return the configured MimeMessageHelper, or null if a MessagingException occurs
   */
  private MimeMessageHelper createtMimeMessageHelper(final MimeMessage message, final EmailMessage details) {
    try {
      // Initialize MimeMessageHelper with mixed-related multipart mode and UTF-8 encoding
      final MimeMessageHelper helper = new MimeMessageHelper(message, MULTIPART_MODE_MIXED_RELATED, UTF_8.name());

      // Set the sender's email address
      helper.setFrom(details.getFrom());
      // Set the recipient's email address
      helper.setTo(details.getTo());
      // Set the email subject
      helper.setSubject(details.getSubject());

      return helper;
    } catch (final MessagingException ex) {
      // Log the exception message and stack trace
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      throw new UnableToCompleteOperationException();
    }
  }

  /**
   * Adds the logo as an inline resource in the email.
   *
   * <p>This method sets the logo file to be displayed inline within the email content.
   * The logo is identified by a specific value key.</p>
   *
   * @param helper the MimeMessageHelper used to set the inline resource
   * @throws MessagingException if there is a problem adding the inline resource
   */
  protected void setLogo(final MimeMessageHelper helper) throws MessagingException {
    // Add the logo file as an inline resource
    helper.addInline(LOGO.getValue(), logoFile);
  }

  /**
   * Sends an email using the provided email details.
   *
   * <p>This method creates a MIME message, initializes it with the provided email details,
   * sets the email message body and logo, and sends the email using the configured mail sender.</p>
   *
   * @param emailMessage the details of the email to be sent
   * @throws UnableToCompleteOperationException if there is an issue with sending the email
   */
  public void sendMessage(final EmailMessage emailMessage) {
    try {
      // Create a new MIME message
      final MimeMessage message = mailSender.createMimeMessage();
      // Initialize the MIME message helper with email details
      final MimeMessageHelper helper = createtMimeMessageHelper(message, emailMessage);
      // Set the email message body
      setEmailBody(helper, emailMessage);
      // Set the logo as an inline resource
      setLogo(helper);

      // Send the email message
      mailSender.send(message);
    } catch (final MessagingException | MailSendException ex) {
      logIfEnabled(log::isErrorEnabled, () -> log.error(ex.getMessage(), ex));
      throw new UnableToCompleteOperationException();
    }
  }

  /**
   * Sends an email using the provided email details.
   *
   * <p>This method creates an {@link EmailMessage} object from the provided parameters,
   * then invokes {@link #sendMessage(EmailMessage)} to send the email.</p>
   *
   * @param from     the sender's email address
   * @param to       the recipient's email address
   * @param subject  the subject of the email
   * @param htmlText the HTML content of the email body
   */
  @Override
  public void sendMessage(final String from, final String to, final String subject, final String htmlText) {
    // Retrieve email details from the message request
    final EmailMessage emailMessage = EmailMessage.of(from, to, subject, htmlText);

    sendMessage(emailMessage);
  }

  /**
   * Sends an email using the provided recipient, subject, and message body.
   *
   * <p>This method constructs an {@link EmailMessage} object with the origin email address
   * as the sender, then invokes {@link #sendMessage(String, String, String, String)} to send the email.</p>
   *
   * @param to          the recipient's email address
   * @param subject     the subject of the email
   * @param messageBody the plain text message body of the email
   */
  @Override
  public void sendMessage(final String to, final String subject, final String messageBody) {
    sendMessage(getOriginEmailAddress(), to, subject, messageBody);
  }


}
