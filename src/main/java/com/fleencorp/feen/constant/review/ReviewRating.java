package com.fleencorp.feen.constant.review;

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
public enum ReviewRating implements ApiParameter {

  EXCELLENT("Excellent", 5, "stream.review.rating.excellent"),
  FAIR("Fair", 2, "stream.review.rating.fair"),
  GOOD("Good", 3, "stream.review.rating.good"),
  POOR("Poor", 1, "stream.review.rating.poor"),
  VERY_GOOD("Very Good", 4, "stream.review.rating.very.good");

  private final String value;
  private final String messageCode;
  private final int ratingNumber;

  ReviewRating(
      final String value,
      final int ratingNumber,
      final String messageCode) {
    this.value = value;
    this.messageCode = messageCode;
    this.ratingNumber = ratingNumber;
  }

  /**
   * Creates an instance of {@link ReviewRating} from a string representation of the rating.
   *
   * @param rating the string representation of the rating (can be the enum name or rating number)
   * @return the corresponding {@link ReviewRating} instance, or null if the rating does not match any enum constant
   */
  public static ReviewRating of(final String rating) {
    try {
      // Try to parse as enum name first
      return ReviewRating.valueOf(rating.toUpperCase());
    } catch (final IllegalArgumentException e) {
      try {
        // Try to parse as rating number
        final int ratingNumber = Integer.parseInt(rating);
        for (final ReviewRating r : values()) {
          if (r.getRatingNumber() == ratingNumber) {
            return r;
          }
        }
      } catch (final NumberFormatException ignored) {
        // Not a number, try to match by value
        for (final ReviewRating r : values()) {
          if (r.getValue().equalsIgnoreCase(rating)) {
            return r;
          }
        }
      }
    }
    return null;
  }
}
