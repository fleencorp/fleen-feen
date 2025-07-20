package com.fleencorp.feen.review.service;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.model.dto.AddReviewDto;
import com.fleencorp.feen.review.model.dto.UpdateReviewDto;
import com.fleencorp.feen.review.model.response.ReviewAddResponse;
import com.fleencorp.feen.review.model.response.ReviewDeleteResponse;
import com.fleencorp.feen.review.model.response.ReviewUpdateResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface ReviewService {

  ReviewAddResponse addReview(AddReviewDto addReviewDto, RegisteredUser user) throws StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException;

  ReviewUpdateResponse updateReview(Long reviewId, UpdateReviewDto updateStreamReviewDto, RegisteredUser user)
    throws ReviewNotFoundException, StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException,
    FailedOperationException;

  ReviewDeleteResponse deleteReview(Long reviewId, RegisteredUser user);

}
