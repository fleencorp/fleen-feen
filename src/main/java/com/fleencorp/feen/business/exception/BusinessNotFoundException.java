package com.fleencorp.feen.business.exception;

import com.fleencorp.localizer.model.exception.LocalizedException;

import java.util.function.Supplier;

public class BusinessNotFoundException extends LocalizedException {

  public BusinessNotFoundException(final Object...params) {
    super(params);
  }

  @Override
  public String getMessageCode() {
    return "business.not.found";
  }

  public static Supplier<BusinessNotFoundException> of(final Long businessId) {
    return () -> new BusinessNotFoundException(businessId);
  }
}
