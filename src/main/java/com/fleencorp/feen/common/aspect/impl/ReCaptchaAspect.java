package com.fleencorp.feen.common.aspect.impl;

import com.fleencorp.feen.adapter.google.recaptcha.model.response.ReCaptchaResponse;
import com.fleencorp.feen.service.impl.external.recaptcha.ReCaptchaService;
import com.fleencorp.feen.service.impl.external.recaptcha.impl.ReCaptchaAttemptService;
import com.fleencorp.feen.user.exception.recaptcha.InvalidReCaptchaException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The ReCaptchaAspect is used on the application APIs to verify that requests coming to the application are
 * made by a real user or human and not a machine. It also protects the application APIs from unnecessary calls in case
 * of invalid security tokens attached to the requests sent to application, it sets a limit on the number of requests to be
 * made at an instance to the application APIs until after a defined period of time.
 *
 * @author Yusuf Alamu Musa
 */
@Slf4j
@Aspect
@Component
public class ReCaptchaAspect {

  private static final String RECAPTCHA_HEADER_KEY = "Recaptcha-token";
  private final ReCaptchaService reCaptchaService;
  private final ReCaptchaAttemptService reCaptchaAttemptService;
  private final String recaptchaScoreThreshold;

  public ReCaptchaAspect(
      final ReCaptchaService reCaptchaService,
      final ReCaptchaAttemptService reCaptchaAttemptService,
      @Value("${google.web.recaptcha-score-threshold}") final String recaptchaScoreThreshold) {
    this.reCaptchaService = reCaptchaService;
    this.reCaptchaAttemptService = reCaptchaAttemptService;
    this.recaptchaScoreThreshold = recaptchaScoreThreshold;
  }

  /**
   * A method annotated with {@code @Around} to verify the reCAPTCHA token before executing the target method.
   * If reCAPTCHA is disabled or if the token is valid, the target method is executed.
   * If the token is invalid, an exception is thrown.
   *
   * @param joinPoint The {@link ProceedingJoinPoint} representing the join point at which reCAPTCHA verification is applied.
   * @return The result of executing the target method if reCAPTCHA verification succeeds.
   * @throws Throwable If an error occurs during reCAPTCHA verification or while executing the target method.
   */
  @Around("@annotation(com.fleencorp.feen.common.aspect.ReCaptcha)")
  public Object verifyReCaptcha(final ProceedingJoinPoint joinPoint) throws Throwable {
    // Check if reCAPTCHA is disabled
    if (isCaptchaDisabled()) {
      // If reCAPTCHA is disabled, proceed with executing the target method
      return joinPoint.proceed();
    }

    // Get the IP address of the client
    final String ipAddress = getClientIpAddress();

    // Check if the IP address is blocked due to too many failed reCAPTCHA attempts
    checkIfIpAddressBlocked(ipAddress);

    // Extract the reCAPTCHA token from the request
    final String reCaptchaToken = extractReCaptchaToken();

    // Verify the reCAPTCHA token
    final ReCaptchaResponse response = verifyReCaptchaToken(reCaptchaToken);

    // If reCAPTCHA verification is successful, proceed with executing the target method
    if (isReCaptchaValid(response)) {
      // Mark the reCAPTCHA attempt as succeeded for the IP address
      reCaptchaAttemptService.reCaptchaSucceeded(ipAddress);
      // Proceed with executing the target method
      return joinPoint.proceed();
    } else {
      // If reCAPTCHA verification fails, mark the reCAPTCHA attempt as failed for the IP address
      reCaptchaAttemptService.reCaptchaFailed(ipAddress);
      // Throw an exception indicating invalid reCAPTCHA
      throw new InvalidReCaptchaException();
    }
  }

  /**
   * Checks if the reCAPTCHA feature is disabled.
   *
   * @return {@code true} if the reCAPTCHA feature is disabled, {@code false} otherwise.
   */
  private boolean isCaptchaDisabled() {
    return getReCaptchaScoreThreshold() <= 0;
  }

  /**
   * Retrieves the IP address of the client making the request.
   *
   * @return The IP address of the client.
   */
  private String getClientIpAddress() {
    final var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    return request.getRemoteAddr();
  }

  /**
   * Checks if the IP address is blocked due to too many failed reCAPTCHA attempts.
   * If the IP address is blocked, an exception is thrown.
   *
   * @param ipAddress The IP address to check.
   * @throws InvalidReCaptchaException If the IP address is blocked due to too many failed reCAPTCHA attempts.
   */
  private void checkIfIpAddressBlocked(final String ipAddress) {
    if (reCaptchaAttemptService.isBlocked(ipAddress)) {
      throw new InvalidReCaptchaException("ReCaptcha attempt failed");
    }
  }

  /**
   * Extracts the reCAPTCHA token from the request header.
   *
   * @return The reCAPTCHA token extracted from the request header.
   */
  private String extractReCaptchaToken() {
    final var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    return request.getHeader(RECAPTCHA_HEADER_KEY);
  }

  /**
   * Verifies the reCAPTCHA token using the reCAPTCHA service.
   *
   * @param reCaptchaToken The reCAPTCHA token to verify.
   * @return The response received from verifying the reCAPTCHA token.
   */
  private ReCaptchaResponse verifyReCaptchaToken(final String reCaptchaToken) {
    return reCaptchaService.verifyReCaptcha(reCaptchaToken);
  }

  /**
   * Checks if the reCAPTCHA response is valid based on the configured threshold.
   *
   * @param response The reCAPTCHA response to validate.
   * @return {@code true} if the reCAPTCHA response is valid, {@code false} otherwise.
   */
  private boolean isReCaptchaValid(final ReCaptchaResponse response) {
    return response != null && response.isSuccess() && response.getScore() >= getReCaptchaScoreThreshold();
  }

  /**
   * Retrieves the configured reCAPTCHA score threshold from the properties.
   *
   * @return The reCAPTCHA score threshold configured in the properties.
   */
  private float getReCaptchaScoreThreshold() {
    return Float.parseFloat(recaptchaScoreThreshold);
  }

}
