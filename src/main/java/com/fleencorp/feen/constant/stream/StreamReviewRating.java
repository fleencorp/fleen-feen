package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

/**
* Enum representing different ratings for stream reviews.
*
* <p>Each rating corresponds to a descriptive value indicating the quality of a stream.</p>
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum StreamReviewRating implements ApiParameter {

  FAIR("Fair"),
  EXCELLENT("Excellent"),
  GOOD("Good"),
  POOR("Poor"),
  VERY_GOOD("Very Good");

  private final String value;

  StreamReviewRating(String value) {
    this.value = value;
  }
}
