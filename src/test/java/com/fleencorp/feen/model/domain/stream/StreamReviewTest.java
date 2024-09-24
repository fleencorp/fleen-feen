package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReviewTest {

  @Test
  void create_empty_stream_review_null() {
    // GIVEN
    final StreamReview streamReview = null;

    // ASSERT
    assertNull(streamReview);
  }

  @Test
  void create_empty_stream_review() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNotNull(streamReview);
  }

  @Test
  void create_stream_review_without_id() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNull(streamReview.getStreamReviewId());
  }

  @Test
  void create_stream_review_with_id() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    streamReview.setStreamReviewId(1L);

    // ASSERT
    assertNotNull(streamReview.getStreamReviewId());
  }

  @Test
  void ensure_stream_review_ids_are_equal() {
    // GIVEN
    final long streamReviewId = 1L;
    final StreamReview streamReview = new StreamReview();
    streamReview.setStreamReviewId(1L);

    // ASSERT
    assertNotNull(streamReview);
    assertEquals(streamReviewId, streamReview.getStreamReviewId());
  }

  @Test
  void ensure_stream_review_ids_are_not_equal() {
    // GIVEN
    final long streamReviewId = 1L;
    final StreamReview streamReview = new StreamReview();
    streamReview.setStreamReviewId(2L);

    // ASSERT
    assertNotNull(streamReview);
    assertNotEquals(streamReviewId, streamReview.getStreamReviewId());
  }

  @Test
  void create_stream_review_without_review() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNull(streamReview.getReview());
  }

  @Test
  void create_stream_review_with_review() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    streamReview.setReview("reviews");

    // ASSERT
    assertNotNull(streamReview.getReview());
  }

  @Test
  void create_stream_review_without_fleen_stream() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNull(streamReview.getFleenStream());
  }

  @Test
  void create_stream_review_with_fleen_stream() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    streamReview.setFleenStream(new FleenStream());

    // ASSERT
    assertNotNull(streamReview.getFleenStream());
  }

  @Test
  void create_stream_review_without_member() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNull(streamReview.getMember());
  }

  @Test
  void create_stream_review_with_member() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    streamReview.setMember(new Member());

    // ASSERT
    assertNotNull(streamReview.getMember());
  }

  //stream review rating
  @Test
  void create_stream_review_without_rating() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();

    // ASSERT
    assertNull(streamReview.getRating());
  }

  @Test
  void create_stream_review_with_stream_rating() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    streamReview.setRating(StreamReviewRating.EXCELLENT);

    // ASSERT
    assertNotNull(streamReview.getRating());
  }
}
