package com.fleencorp.feen.service.impl.external.aws;

import com.fleencorp.base.service.i18n.LocalizedResponse;
import com.fleencorp.feen.model.dto.aws.VerifyEmailIdentityDto;
import com.fleencorp.feen.model.response.external.aws.VerifyEmailIdentityResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.VerifyEmailIdentityRequest;

@Service
public class SesService {

  private final SesClient sesClient;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of {@code SesService} with the specified SES client
   * and localized response handler.
   *
   * <p>This constructor initializes the {@code SesService} with the provided
   * {@link SesClient} to interact with AWS SES for email operations, and a
   * {@link LocalizedResponse} to handle localized responses based on user locale
   * settings.</p>
   *
   * @param sesClient The {@link SesClient} used for making requests to AWS SES.
   * @param localizedResponse The {@link LocalizedResponse} used to generate
   *                          localized responses for operations performed by this service.
   */
  public SesService(
    final SesClient sesClient,
    final LocalizedResponse localizedResponse) {
    this.sesClient = sesClient;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Verifies the email addresses provided in the {@link VerifyEmailIdentityDto}.
   * If the email addresses are valid, it sends a verification request to AWS SES
   * for each valid email address.
   *
   * <p>The method checks if the email addresses are valid using
   * {@code verifyEmailIdentityDto.isEmailsValid()}. It iterates over the email
   * addresses and validates each one using the {@code isEmailValid(String email)} method.
   * For each valid email address, a {@link VerifyEmailIdentityRequest} is built and sent
   * to the AWS SES client to initiate the verification process.</p>
   *
   * @param verifyEmailIdentityDto A data transfer object containing the email addresses
   *                               to be verified.
   * @return A {@link VerifyEmailIdentityResponse} indicating the result of the verification
   *         process, typically containing localized information about the outcome.
   */
  public VerifyEmailIdentityResponse verifyEmailIdentity(final VerifyEmailIdentityDto verifyEmailIdentityDto) {
    if (verifyEmailIdentityDto.isEmailsValid()) {
      for (final String email : verifyEmailIdentityDto.getEmailAddresses()) {
        final VerifyEmailIdentityRequest request = VerifyEmailIdentityRequest.builder()
          .emailAddress(email)
          .build();

        sesClient.verifyEmailIdentity(request);
      }
    }
    return localizedResponse.of(VerifyEmailIdentityResponse.of());
  }
}
