package com.fleencorp.feen.review.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.EnumOrdinalValid;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.constant.ReviewRating;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

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

  @NotNull(message = "{review.parent.NotNull}")
  @JsonProperty("parent")
  private ReviewParentDto parent;

  public ReviewRating getRating() {
    return ReviewRating.of(rating);
  }

  public boolean hasParent() {
    return nonNull(parent) && nonNull(parent.getParentId()) && nonNull(parent.getParentType());
  }

  public Long getParentId() {
    return nonNull(parent) ? parent.getParentId() : null;
  }

  public ReviewParentType getParentType() {
    return hasParent() ? parent.getParentType() : null;
  }

  public boolean isStreamParent() {
    return hasParent() && parent.isStreamParent();
  }

  public Review toReview(final Member author, final String parentTitle, final ChatSpace chatSpace, final FleenStream stream) {
    final Long parentId = hasParent() ? getParentId() : null;
    final ReviewParentType parentType = hasParent() ? getParentType() : null;

    final Review newReview = new Review();
    newReview.setReviewText(review);
    newReview.setRating(getRating());

    newReview.setParentId(getParentId());
    newReview.setParentTitle(parentTitle);
    newReview.setReviewParentType(parentType);

    newReview.setAuthorId(author.getMemberId());
    newReview.setAuthor(author);

    newReview.setChatSpaceId(parentId);
    newReview.setChatSpace(chatSpace);

    newReview.setStreamId(parentId);
    newReview.setStream(stream);

    return newReview;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class ReviewParentDto {

    @IsNumber(message = "{review.parentId.IsNumber}")
    @JsonProperty("parent_id")
    private String parentId;

    @NotNull(message = "{review.parentType.NotNull}")
    @OneOf(enumClass = ReviewParentType.class, message = "{review.parentType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("parent_type")
    private String parentType;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public ReviewParentType getParentType() {
      return ReviewParentType.of(parentType);
    }

    public boolean isStreamParent() {
      return ReviewParentType.isStream(getParentType());
    }

    public static AddPollDto.PollParentDto of() {
      return new AddPollDto.PollParentDto();
    }
  }
}
