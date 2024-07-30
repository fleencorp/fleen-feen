package com.fleencorp.feen.service.external.recaptcha.impl;

import com.fleencorp.feen.adapter.google.recaptcha.ReCaptchaAdapter;
import com.fleencorp.feen.adapter.google.recaptcha.model.response.ReCaptchaResponse;
import com.fleencorp.feen.aspect.MeasureExecutionTime;
import com.fleencorp.feen.service.external.recaptcha.ReCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReCaptchaServiceImpl implements ReCaptchaService {

  private final ReCaptchaAdapter reCaptchaAdapter;

  public ReCaptchaServiceImpl(final ReCaptchaAdapter reCaptchaAdapter) {
    this.reCaptchaAdapter = reCaptchaAdapter;
  }

  /**
   * It verifies the validity of the reCaptcha details
   *
   * @param reCaptchaToken the token to check and validate in the ReCaptcha service
   * @return the results of the token verification
   */
  @Override
  @MeasureExecutionTime
  public ReCaptchaResponse verifyReCaptcha(final String reCaptchaToken) {
    return reCaptchaAdapter.verifyRecaptcha(reCaptchaToken);
  }
}
