package com.fleencorp.feen.service.impl.common;

import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.service.auth.PasswordService;
import com.fleencorp.feen.service.common.MiscService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Qualifier("misc")
public class MiscServiceImpl implements
    MiscService, PasswordService {

  private final PasswordEncoder passwordEncoder;

  public MiscServiceImpl(final PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public GetEncodedPasswordResponse getEncodedPassword(final String password) {
    return new GetEncodedPasswordResponse(createEncodedPassword(password), password);
  }

  @Override
  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }
}
