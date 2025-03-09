package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.review.StreamReviewRating;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

/**
 * Entity representing a review for a stream in the system.
 * This class manages user reviews and ratings for streams, including the review text,
 * rating value, and associations with the stream and the reviewer.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stream_review")
public class StreamReview extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "review_id", nullable = false, updatable = false, unique = true)
  private Long reviewId;

  @Column(name = "review", nullable = false, length = 1000)
  private String review;

  @ManyToOne(fetch = EAGER, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id", nullable = false, updatable = false)
  private FleenStream stream;

  @CreatedBy
  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(ORDINAL)
  @Column(name = "rating", nullable = false)
  private StreamReviewRating rating;

  /**
   * Retrieves the rating number.
   *
   * <p>This method returns the numeric value of the rating (1-5).
   * If the {@code rating} is {@code null}, the method returns {@code null}.</p>
   *
   * @return the rating number (1-5), or {@code null} if the rating is not set
   */
  public Integer getRatingNumber() {
    return nonNull(rating) ? rating.getRatingNumber() : null;
  }

  /**
   * Retrieves the name of the rating associated with this review.
   *
   * @return the name of the rating, or null if the rating is not set.
   */
  public String getRatingName() {
    return nonNull(rating) ? rating.getValue() : null;
  }

  /**
   * Retrieves the username of the reviewer associated with this review.
   *
   * @return the username of the reviewer, or null if the reviewer is not set.
   */
  public String getReviewerName() {
    return nonNull(member) ? member.getUsername() : null;
  }

  /**
   * Retrieves the profile photo URL of the reviewer associated with this review.
   *
   * @return the URL of the reviewer's profile photo, or null if the reviewer is not set.
   */
  public String getReviewerPhoto() {
    return nonNull(member) ? member.getProfilePhotoUrl() : null;
  }

  /**
   * Updates the review text and rating.
   *
   * @param review the new review text
   * @param rating the new rating value
   */
  public void update(final String review, final StreamReviewRating rating) {
    this.review = review;
    this.rating = rating;
  }

  /**
   * Creates a new StreamReview instance with the specified details.
   *
   * @param stream the stream being reviewed
   * @param member the member writing the review
   * @param review the review text
   * @param rating the rating value
   * @return a new StreamReview instance
   */
  public static StreamReview of(
      final FleenStream stream,
      final Member member,
      final String review,
      final StreamReviewRating rating) {
    final StreamReview streamReview = new StreamReview();
    streamReview.setStream(stream);
    streamReview.setMember(member);
    streamReview.setReview(review);
    streamReview.setRating(rating);

    return streamReview;
  }

  /**
   * Returns an empty StreamReview instance (null).
   *
   * @return null
   */
  public static StreamReview empty() {
    return null;
  }
}
