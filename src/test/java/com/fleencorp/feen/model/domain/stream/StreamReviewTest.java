package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReviewTest {

  //null test
  @Test
  void ensure_stream_review_is_null() {
    // GIVEN
    final StreamReview streamReview = null;
    // ASSERT
    assertNull(streamReview, "Stream review should be null");
  }

  //not null test
  @Test
  void ensure_stream_review_is_not_null() {
    // GIVEN
    final StreamReview streamReview = new StreamReview();
    // ASSERT
    assertNotNull(streamReview, "Stream review should not be null");
  }

  //equals test
  @Test
  void ensure_equality_of_stream_review() {
    // GIVEN
    final StreamReview streamReview1 = new StreamReview(1L, "streams", new FleenStream(), new Member(), StreamReviewRating.EXCELLENT);
    final StreamReview streamReview2 = new StreamReview(1L, "streams", new FleenStream(), new Member(), StreamReviewRating.EXCELLENT);

    // ASSERT
    assertEquals(streamReview1, streamReview2, "Both streams should be equal");

  }

  //not equals test
  @Test
  void ensure_non_equality_of_stream_review() {
    // GIVEN
    final StreamReview streamReview1 = new StreamReview(2L, "streams", new FleenStream(), new Member(), StreamReviewRating.EXCELLENT);
    final StreamReview streamReview2 = new StreamReview(1L, "streams", new FleenStream(), new Member(), StreamReviewRating.EXCELLENT);

    // ASSERT
    assertEquals(streamReview1, streamReview2, "Both streams should not be equal");

  }
}
