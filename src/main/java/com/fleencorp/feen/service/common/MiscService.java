package com.fleencorp.feen.service.common;

import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;

public interface MiscService {

  GetEncodedPasswordResponse getEncodedPassword(String password);
}
