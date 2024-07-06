package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different state or timeline of a stream.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamTimelineStatus implements ApiParameter {

  FINISHED("Finished"),
  ONGOING("Ongoing"),
  SCHEDULED("Scheduled");

  private final String value;

  StreamTimelineStatus(String value) {
    this.value = value;
  }
}
