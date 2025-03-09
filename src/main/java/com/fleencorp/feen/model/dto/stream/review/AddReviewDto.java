package com.fleencorp.feen.model.dto.stream.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.EnumOrdinalValid;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.constant.review.ReviewRating;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.constant.review.ReviewType.STREAM;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddReviewDto {

  @NotBlank(message = "{review.review.NotBlank}")
  @Size(min = 10, max = 1000, message = "{review.review.Size}")
  @JsonProperty("review")
  private String review;

  @NotNull(message = "{review.rating.NotNull}")
  @IsNumber
  @EnumOrdinalValid(enumClass = ReviewRating.class, message = "{review.rating.Type}")
  @JsonProperty("rating")
  private String rating;

  public ReviewRating getRating() {
    return ReviewRating.of(rating);
  }

  public Review toStreamReview(final FleenStream stream, final Member member) {
    final Review review = toStreamReview();
    review.setStream(stream);
    review.setMember(member);
    review.setReviewType(STREAM);

    return review;
  }

  public Review toStreamReview() {
    final Review review = new Review();
    review.setReview(this.review);
    review.setRating(getRating());

    return review;
  }
}
