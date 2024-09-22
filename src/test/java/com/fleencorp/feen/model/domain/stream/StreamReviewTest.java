package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReviewTest {
  @Test
  void ensure_stream_review_is_null() {
    final StreamReview streamReview = null;
    assertNull(streamReview);
  }

  @Test
  void ensure_stream_review_is_not_null() {
    final StreamReview streamReview = new StreamReview();
    assertNotNull(streamReview);
  }

  @Test
  void ensure_stream_review_are_not_equal() {
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setStreamReviewId(1l);
    final StreamReview streamReview2 = new StreamReview();
    streamReview1.setStreamReviewId(2l);


    assertNotEquals(streamReview1, streamReview2);
  }

  @Test
  void ensure_stream_review_are_equal() {
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setStreamReviewId(1l);
    final StreamReview streamReview2 = new StreamReview();
    streamReview2.setStreamReviewId(1l);
    assertNotNull(streamReview1);

    assertEquals(streamReview1.getStreamReviewId(), streamReview2.getStreamReviewId());
  }


  @Test
  void ensure_stream_review_id_is_equal() {
    // GIVEN
    final long stresmReviewId = 1L;
    final StreamReview streamReview = new StreamReview();
    streamReview.setStreamReviewId(1L);

    // ASSERT
    assertNotNull(streamReview);
    assertEquals(stresmReviewId, streamReview.getStreamReviewId());

  }

  @Test
  void ensure_stream_review_id_is_not_equal() {
    // GIVEN
    final long stresmReviewId = 1L;
    final StreamReview streamReview = new StreamReview();
    streamReview.setStreamReviewId(2L);

    // ASSERT
    assertNotNull(streamReview);
    assertNotEquals(stresmReviewId, streamReview.getStreamReviewId());

  }
}
