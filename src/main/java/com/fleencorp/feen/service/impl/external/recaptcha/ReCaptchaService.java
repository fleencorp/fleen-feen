package com.fleencorp.feen.service.impl.external.recaptcha;

import com.fleencorp.feen.adapter.google.recaptcha.model.response.ReCaptchaResponse;

public interface ReCaptchaService {

  ReCaptchaResponse verifyReCaptcha(String reCaptchaToken);
}
