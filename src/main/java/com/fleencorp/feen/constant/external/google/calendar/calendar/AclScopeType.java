package com.fleencorp.feen.constant.external.google.calendar.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing types of ACL scopes.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum AclScopeType implements ApiParameter {

  DEFAULT("default"),
  USER("user"),
  GROUP("group"),
  DOMAIN("domain");

  private final String value;

  AclScopeType(String value) {
    this.value = value;
  }
}
