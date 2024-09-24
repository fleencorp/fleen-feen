package com.fleencorp.feen.constant.stream;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.getEnumConstant;

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

  POOR("Poor"),
  FAIR("Fair"),
  GOOD("Good"),
  VERY_GOOD("Very Good"),
  EXCELLENT("Excellent");

  private final String value;

  StreamReviewRating(final String value) {
    this.value = value;
  }

  /**
   * Creates an instance of {@link StreamReviewRating} from a string representation of the rating.
   *
   * @param rating the string representation of the rating, which must be convertible to an integer.
   * @return the corresponding {@link StreamReviewRating} instance, or null if the rating does not match any enum constant.
   * @throws NumberFormatException if the string cannot be parsed as an integer.
   */
  public static StreamReviewRating of(final String rating) {
    return getEnumConstant(StreamReviewRating.class, Integer.parseInt(rating));
  }
}
