package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import static jakarta.persistence.EnumType.ORDINAL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stream_review")
@EqualsAndHashCode
public class StreamReview extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_review_id", nullable = false, updatable = false, unique = true)
  private Long streamReviewId;

  @Column(name = "review", nullable = false, length = 1000)
  private String review;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id", nullable = false, updatable = false)
  private FleenStream fleenStream;

  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(ORDINAL)
  @Column(name = "rating", nullable = false)
  private StreamReviewRating rating;
}
