package com.fleencorp.feen.review.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.review.constant.ReviewParentType;
import com.fleencorp.feen.review.constant.ReviewRating;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
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
@Table(name = "review")
public class Review extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "review_id", nullable = false, updatable = false, unique = true)
  private Long reviewId;

  @Column(name = "review", nullable = false, length = 1000)
  private String reviewText;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Column(name = "parent_title", updatable = false)
  private String parentTitle;

  @Enumerated(STRING)
  @Column(name = "parent_type", nullable = false)
  private ReviewParentType reviewParentType;

  @Column(name = "stream_id", updatable = false, insertable = false)
  private Long streamId;

  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "stream_id", referencedColumnName = "stream_id", updatable = false)
  private FleenStream stream;

  @Column(name = "chat_space_id", updatable = false, insertable = false)
  private Long chatSpaceId;

  @ManyToOne(fetch = LAZY, targetEntity = FleenStream.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "author_id", insertable = false, updatable = false)
  private Long authorId;

  @CreatedBy
  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "author_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member author;

  @Enumerated(ORDINAL)
  @Column(name = "rating", nullable = false)
  private ReviewRating rating;

  @Column(name = "like_count", nullable = false)
  private Long likeCount = 0L;

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
    return nonNull(author) ? author.getUsername() : null;
  }

  /**
   * Retrieves the profile photo URL of the reviewer associated with this review.
   *
   * @return the URL of the reviewer's profile photo, or null if the reviewer is not set.
   */
  public String getReviewerPhoto() {
    return nonNull(author) ? author.getProfilePhotoUrl() : null;
  }

  /**
   * Updates the review text and rating.
   *
   * @param review the new review text
   * @param rating the new rating value
   */
  public void update(final String review, final ReviewRating rating) {
    this.reviewText = review;
    this.rating = rating;
  }
}
