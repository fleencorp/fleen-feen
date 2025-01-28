package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stream_review")
public class StreamReview extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_review_id", nullable = false, updatable = false, unique = true)
  private Long streamReviewId;

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
   * Retrieves the rating number based on the rating's ordinal value.
   *
   * <p>This method returns the rating number as an integer, which is calculated by taking
   * the ordinal value of the {@code rating} and adding 1 because ordinals in Java Enum start counting from 0.
   * If the {@code rating} is {@code null}, the method returns {@code null}.</p>
   *
   * @return the rating number (ordinal value + 1), or {@code null} if the rating is not set.
   */
  public Integer getRatingNumber() {
    return nonNull(rating) ? rating.ordinal() + 1 : null;
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
   * Retrieves the title of the stream associated with this review.
   *
   * @return the title of the stream, or null if the stream is not set.
   */
  public String getStreamTitle() {
    return nonNull(stream) ? stream.getTitle() : null;
  }

  /**
   * Retrieves the full name of the reviewer associated with this review.
   *
   * @return the full name of the reviewer, or null if the reviewer is not set.
   */
  public String getReviewerName() {
    return nonNull(member) ? member.getFullName() : null;
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
   * Retrieves the stream ID.
   *
   * @return the stream ID if the stream is not null; otherwise, null.
   */
  public Long getStreamId() {
    return nonNull(stream) ? stream.getStreamId() : null;
  }
}
