package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.EnumOrdinalValid;
import com.fleencorp.feen.constant.stream.StreamReviewRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStreamDto {

  @NotNull(message = "{stream.rating.NotNull}")
  @EnumOrdinalValid(enumClass = StreamReviewRating.class, message = "{stream.rating.Type}")
  @JsonProperty("rating")
  private String rating;

  @Size(min = 10, max = 500, message = "{stream.comment.Size}")
  @JsonProperty("comment")
  protected String comment;
}


