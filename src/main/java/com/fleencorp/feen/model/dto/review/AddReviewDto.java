package com.fleencorp.feen.model.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.EnumOrdinalValid;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.review.ReviewParentType;
import com.fleencorp.feen.constant.review.ReviewRating;
import com.fleencorp.feen.model.domain.review.Review;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
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

  @NotNull(message = "{review.parentType.NotNull}")
  @OneOf(enumClass = ReviewParentType.class, message = "{review.parentType.Type}")
  @JsonProperty("review_parent_type")
  private String reviewParentType;

  @IsNumber(message = "review.parentId.IsNumber")
  @JsonProperty("parent_id")
  protected String parentId;

  public ReviewRating getRating() {
    return ReviewRating.of(rating);
  }

  public ReviewParentType getReviewParentType() {
    return ReviewParentType.of(reviewParentType);
  }

  public Long getParentId() {
    return Long.parseLong(parentId);
  }

  public boolean isStreamReviewType() {
    return ReviewParentType.isStream(getReviewParentType());
  }

  public Review toStreamReview(final FleenStream stream, final Member member) {
    final Review newReview = toReview();
    newReview.setParentId(getParentId());
    newReview.setParentTitle(stream.getTitle());
    newReview.setReviewParentType(getReviewParentType());
    newReview.setStreamId(stream.getStreamId());
    newReview.setStream(stream);
    newReview.setMemberId(member.getMemberId());
    newReview.setMember(member);

    return newReview;
  }

  public Review toReview() {
    final Review newReview = new Review();
    newReview.setReview(review);
    newReview.setRating(getRating());

    return newReview;
  }
}
