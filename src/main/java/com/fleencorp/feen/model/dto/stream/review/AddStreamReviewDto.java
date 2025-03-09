package com.fleencorp.feen.model.dto.stream.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.EnumOrdinalValid;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.constant.stream.review.StreamReviewRating;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamReview;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddStreamReviewDto {

  @NotBlank(message = "{review.review.NotBlank}")
  @Size(min = 10, max = 1000, message = "{review.review.Size}")
  private String review;

  @NotNull(message = "{review.rating.NotNull}")
  @IsNumber
  @EnumOrdinalValid(enumClass = StreamReviewRating.class, message = "{review.rating.Type}")
  @JsonProperty("review_rating")
  private String rating;

  public StreamReviewRating getRating() {
    return StreamReviewRating.of(rating);
  }

  public StreamReview toStreamReview(final FleenStream stream, final Member member) {
    final StreamReview streamReview = toStreamReview();
    streamReview.setStream(stream);
    streamReview.setMember(member);

    return streamReview;
  }

  public StreamReview toStreamReview() {
    final StreamReview streamReview = new StreamReview();
    streamReview.setReview(review);
    streamReview.setRating(getRating());

    return streamReview;
  }
}
