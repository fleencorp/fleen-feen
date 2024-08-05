package com.fleencorp.feen.constant.base;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

@Getter
public enum ReportMessageType implements ApiParameter {

  ERROR("Error"),
  INFO("Info"),
  WARN("Warn"),
  GOOGLE_CALENDAR("Google Calendar"),
  GOOGLE_OAUTH2("Google Oauth2"),
  YOUTUBE("YouTube"),
  PROFILE_VERIFICATION("Profile Verification"),
  GENERAL("General");

  private final String value;

  ReportMessageType(final String value) {
    this.value = value;
  }
}
