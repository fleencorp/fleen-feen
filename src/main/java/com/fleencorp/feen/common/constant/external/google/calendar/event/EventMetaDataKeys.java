package com.fleencorp.feen.common.constant.external.google.calendar.event;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing the keys used for event metadata in API parameters.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum EventMetaDataKeys implements ApiParameter {

  TAGS("tags");

  private final String value;

  EventMetaDataKeys(final String value) {
    this.value = value;
  }
}
