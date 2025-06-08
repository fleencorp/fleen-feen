package com.fleencorp.feen.adapter.google.oauth2.model.constant;

import com.fleencorp.base.constant.base.EndpointBlock;
import lombok.Getter;

@Getter
public enum GoogleOauth2EndpointBlock implements EndpointBlock {

  USER_INFO("/userinfo"),;

  private final String value;

  GoogleOauth2EndpointBlock(final String value) {
    this.value = value;
  }

  @Override
  public String value() {
    return value;
  }
}
