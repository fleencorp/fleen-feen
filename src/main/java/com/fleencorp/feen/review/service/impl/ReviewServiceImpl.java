package com.fleencorp.feen.review.service.impl;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.exception.core.CannotAddReviewIfStreamHasNotStartedException;
import com.fleencorp.feen.review.exception.core.ReviewNotFoundException;
import com.fleencorp.feen.review.mapper.ReviewMapper;
import com.fleencorp.feen.review.model.domain.Review;
import com.fleencorp.feen.review.model.dto.AddReviewDto;
import com.fleencorp.feen.review.model.dto.UpdateReviewDto;
import com.fleencorp.feen.review.model.holder.ReviewParentDetailHolder;
import com.fleencorp.feen.review.model.response.ReviewAddResponse;
import com.fleencorp.feen.review.model.response.ReviewDeleteResponse;
import com.fleencorp.feen.review.model.response.ReviewUpdateResponse;
import com.fleencorp.feen.review.model.response.base.ReviewResponse;
import com.fleencorp.feen.review.repository.ReviewRepository;
import com.fleencorp.feen.review.service.ReviewCommonService;
import com.fleencorp.feen.review.service.ReviewService;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.core.StreamService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;
import static java.util.Objects.nonNull;

/**
 * Implementation of the {@link ReviewService} interface for managing stream reviews.
 *
 * <p>This class provides methods to add, delete, and search for stream reviews, leveraging
 * repositories for data persistence and localization services for user-friendly responses.</p>
 *
 * <p>It encapsulates the business logic related to stream reviews and acts as a bridge
 * between the application and the underlying data access layer.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class ReviewServiceImpl implements ReviewService {

  private final MemberService memberService;
  private final ReviewCommonService reviewCommonService;
  private final StreamService streamService;
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code ReviewServiceImpl}, responsible for managing reviews and related interactions
   * such as likes on reviews within streams.
   *
   * @param memberService the service for managing members
   * @param reviewCommonService the service used to handle common review operations
   * @param streamService the service for retrieving and validating stream-related data associated with reviews
   * @param reviewRepository the repository for performing CRUD operations on review entities
   * @param reviewMapper the mapper for converting between review entities and their corresponding DTOs
   * @param localizer the utility for resolving localized text for responses and messages
   */
  public ReviewServiceImpl(
      @Lazy final MemberService memberService,
      @Lazy final ReviewCommonService reviewCommonService,
      final StreamService streamService,
      final ReviewRepository reviewRepository,
      final ReviewMapper reviewMapper,
      final Localizer localizer) {
    this.memberService = memberService;
    this.reviewCommonService = reviewCommonService;
    this.streamService = streamService;
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
    this.localizer = localizer;
  }

  /**
   * Adds a review for the specified event or stream.
   *
   * <p>This method retrieves the event or stream by its ID, throwing a {@link StreamNotFoundException}
   * if the stream does not exist. It then ensures the stream has started or completed before allowing
   * the review to be added. Finally, the review is created and saved using the provided data.</p>
   *
   * @param addReviewDto the data transfer object containing the details of the review to be added
   * @param user the current user adding the review, used to associate the review with a member
   * @return an {@link ReviewAddResponse} indicating the result of the review addition
   * @throws StreamNotFoundException if no event or stream is found with the specified ID
   * @throws CannotAddReviewIfStreamHasNotStartedException if the stream has not yet started and cannot be reviewed
   */
  @Override
  @Transactional
  public ReviewAddResponse addReview(final AddReviewDto addReviewDto, final RegisteredUser user)
      throws StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException {
    final Member member = memberService.findMember(user.getId());

    final ReviewParentDetailHolder reviewParentDetailHolder = retrieveReviewOtherDetailsHolder(addReviewDto);
    final String parentTitle = reviewParentDetailHolder.parentTitle();
    final ChatSpace chatSpace = reviewParentDetailHolder.chatSpace();
    final FleenStream stream = reviewParentDetailHolder.stream();

    final Review review = addReviewDto.toReview(member, parentTitle, chatSpace, stream);
    reviewRepository.save(review);

    final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
    setEntityUpdatableByUser(reviewResponse, user.getId());

    final ReviewAddResponse reviewAddResponse = ReviewAddResponse.of(reviewResponse);
    return localizer.of(reviewAddResponse);
  }

  /**
   * Updates an existing review for the specified event or stream.
   *
   * <p>This method finds the review by its ID, the associated stream, and the member who added it.
   * If the review is not found, a {@link ReviewNotFoundException} is thrown. The review is then
   * updated with the provided details, and the changes are saved.</p>
   *
   * @param reviewId the ID of the review to be updated
   * @param updateReviewDto the data transfer object containing the updated review details
   * @param user the current user updating the review, used to verify the ownership of the review
   * @return an {@link ReviewUpdateResponse} indicating the result of the review update
   * @throws ReviewNotFoundException if no review is found with the specified ID for the stream and member
   * @throws StreamNotFoundException if no stream is found with the specified stream ID
   */
  @Override
  @Transactional
  public ReviewUpdateResponse updateReview(final Long reviewId, final UpdateReviewDto updateReviewDto, final RegisteredUser user)
    throws ReviewNotFoundException, StreamNotFoundException, CannotAddReviewIfStreamHasNotStartedException,
      FailedOperationException {
    final Member member = user.toMember();

    if (updateReviewDto.isStreamParent()) {
      final Review review = reviewRepository.findByReviewIdAndStreamAndMember(reviewId, updateReviewDto.getParentId(), member)
        .orElseThrow(ReviewNotFoundException.of(reviewId));

      review.update(updateReviewDto.getReview(), updateReviewDto.getRating());
      reviewRepository.save(review);

      final ReviewResponse reviewResponse = reviewMapper.toReviewResponsePublic(review);
      reviewCommonService.processReviewsOtherDetails(List.of(reviewResponse), member);
      final ReviewUpdateResponse reviewUpdateResponse = ReviewUpdateResponse.of(reviewResponse);

      return localizer.of(reviewUpdateResponse);
    }

    throw new ReviewNotFoundException(reviewId);
  }

  /**
   * Retrieves the {@link ReviewParentDetailHolder} based on the given {@link AddReviewDto}.
   *
   * <p>This method first checks that the provided {@code addReviewDto} is not {@code null}. It then evaluates
   * whether the review is associated with a stream by calling {@link AddReviewDto#isStreamParent()}. If so,
   * it attempts to retrieve and validate the parent {@link FleenStream} using the provided parent ID and
   * returns the corresponding {@link ReviewParentDetailHolder}.</p>
   *
   * @param addReviewDto the DTO containing information about the review, including its parent type and ID
   * @return a {@link ReviewParentDetailHolder} containing additional details related to the review
   * @throws FailedOperationException if the {@code addReviewDto} is {@code null} or the review parent type is invalid
   */
  protected ReviewParentDetailHolder retrieveReviewOtherDetailsHolder(final AddReviewDto addReviewDto) throws FailedOperationException {
    // Ensure reviewParentType is not null
    checkIsNull(addReviewDto, FailedOperationException::new);

    // If the reviewParentType is a stream, retrieve the associated FleenStream
     if (addReviewDto.isStreamParent()) {
       return validateStreamParent(addReviewDto.getParentId(), addReviewDto.getParentType());
     }

     throw FailedOperationException.of();
  }

  /**
   * Validates and retrieves the parent details for a review based on a stream ID and parent type.
   *
   * <p>This method checks whether the given stream ID is non-null and attempts to find the
   * corresponding {@link FleenStream}. It then verifies that the stream is eligible to
   * receive a review by calling {@code checkAddReviewEligibility()}. If valid, a new
   * {@link ReviewParentDetailHolder} is returned containing the stream and the specified
   * parent type. If the stream ID is null, a {@link FailedOperationException} is thrown.</p>
   *
   * @param streamId   the ID of the stream to validate, must not be {@code null}
   * @param parentType the type of the review parent to associate with the stream
   * @return a {@link ReviewParentDetailHolder} containing the validated stream and parent type
   * @throws FailedOperationException if the stream ID is {@code null} or validation fails
   */
  protected ReviewParentDetailHolder validateStreamParent(final Long streamId, final ReviewParentType parentType) {
    if (nonNull(streamId)) {
      final FleenStream stream = streamService.findStream(streamId);
      stream.checkAddReviewEligibility();

      return ReviewParentDetailHolder.of(null, stream, parentType);
    }

    throw FailedOperationException.of();
  }

  /**
   * Deletes a stream review by its ID and the associated member.
   *
   * <p>This method attempts to delete a review with the specified ID, associated with the current user.
   * If the review is found and successfully deleted, a localized {@link ReviewDeleteResponse} is returned.</p>
   *
   * @param reviewId the ID of the stream review to delete
   * @param user the current user attempting to delete the review, used to ensure only the user's review is deleted
   * @return a {@link ReviewDeleteResponse} indicating the outcome of the deletion
   */
  @Override
  @Transactional
  public ReviewDeleteResponse deleteReview(final Long reviewId, final RegisteredUser user) {
    reviewRepository.deleteByStreamReviewIdAndMember(reviewId, user.toMember());

    final ReviewDeleteResponse reviewDeleteResponse = ReviewDeleteResponse.of();
    return localizer.of(reviewDeleteResponse);
  }
}