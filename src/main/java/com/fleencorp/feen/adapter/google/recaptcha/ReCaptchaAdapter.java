package com.fleencorp.feen.adapter.google.recaptcha;

import com.fleencorp.base.adapter.base.BaseAdapter;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.exception.externalsystem.ExternalSystemException;
import com.fleencorp.feen.adapter.google.recaptcha.model.constant.GoogleRecaptchaEndpointBlock;
import com.fleencorp.feen.adapter.google.recaptcha.model.constant.GoogleRecaptchaParameter;
import com.fleencorp.feen.adapter.google.recaptcha.model.response.ReCaptchaResponse;
import com.fleencorp.feen.common.constant.external.ExternalSystemType;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.fleencorp.feen.common.util.LoggingUtil.logIfEnabled;

/**
 * The ReCaptchaAdapter is used for authentication and security purpose to verify that
 * the request being sent to the application is made by a human and not a bot or machine.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Component
public class ReCaptchaAdapter extends BaseAdapter {

  /**
   * The secret key required for communicating with the reCAPTCHA service.
   */
  @NotBlank
  private final String recaptchaSecret;

  /**
   * Constructor for ReCaptchaAdapter class.
   *
   * @param baseUrl    The base URL of the reCAPTCHA service
   * @param secretKey  The secret key used for reCAPTCHA verification
   */
  protected ReCaptchaAdapter(
      @Value("${google.recaptcha.base-url}") final String baseUrl,
      @Value("${google.recaptcha.secret-key}") final String secretKey,
      final RestClient restClient) {
    super(baseUrl, new RestTemplateBuilder()
      .requestFactory(SimpleClientHttpRequestFactory::new).build(), restClient);
    this.recaptchaSecret = secretKey;
  }

  /**
   * Verifies a reCAPTCHA token with the Google reCAPTCHA service.
   *
   * @param reCaptchaToken The reCAPTCHA token to be verified
   * @return {@link ReCaptchaResponse} object containing verification results
   * @throws ExternalSystemException if an error occurs during the verification process
   *
   * @see <a href="https://developers.google.com/recaptcha/docs/verify">Verifying the user's response</a>
   * @see <a href="https://cloud.google.com/recaptcha-enterprise/docs/create-key-website">Create reCAPTCHA keys for websites</a>
   */
  public ReCaptchaResponse verifyRecaptcha(final String reCaptchaToken) {
    final Map<ApiParameter, String> parameters = new ConcurrentHashMap<>();

    parameters.put(GoogleRecaptchaParameter.SECRET, recaptchaSecret);
    parameters.put(GoogleRecaptchaParameter.RESPONSE, reCaptchaToken);

    final URI uri = buildUri(parameters, GoogleRecaptchaEndpointBlock.SITE_VERIFY);
    final ResponseEntity<ReCaptchaResponse> response = doCall(uri, HttpMethod.POST,
        null, null, ReCaptchaResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      logIfEnabled(log::isErrorEnabled, () -> log.error("An error occurred while calling verifyRecaptcha method of RecaptchaAdapter: {}", response.getBody()));
      throw new ExternalSystemException(ExternalSystemType.GOOGLE_RECAPTCHA.getValue());
    }
  }

}
