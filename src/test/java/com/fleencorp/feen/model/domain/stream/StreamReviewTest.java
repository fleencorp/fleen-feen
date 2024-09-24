package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamReviewRating;
import com.fleencorp.feen.model.domain.auth.Oauth2Authorization;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReviewTest {
  @Test
  void create_empty_stream_review_null() {
    final StreamReview streamReview = null;
    assertNull(streamReview);
  }

  @Test
  void create_empty_stream_review() {
    final StreamReview streamReview = new StreamReview();
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

//  @Test
//  void ensure_stream_review_are_not_equal() {
//    final StreamReview streamReview1 = new StreamReview();
//    streamReview1.setStreamReviewId(1l);
//    final StreamReview streamReview2 = new StreamReview();
//    streamReview1.setStreamReviewId(2l);
//
//
//    assertNotEquals(streamReview1, streamReview2);
//  }
//
//  @Test
//  void ensure_stream_review_are_equal() {
//    final StreamReview streamReview1 = new StreamReview();
//    streamReview1.setStreamReviewId(1l);
//    final StreamReview streamReview2 = new StreamReview();
//    streamReview2.setStreamReviewId(1l);
//    assertNotNull(streamReview1);
//
//    assertEquals(streamReview1.getStreamReviewId(), streamReview2.getStreamReviewId());
//  }


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

//reviews
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
  void ensure_stream_review_reviews_are_equal() {
    // GIVEN
    final String review = "reviews";
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setReview("reviews");

    // ASSERT
    assertNotNull(streamReview1);
    assertEquals(review, streamReview1.getReview());
  }

  @Test
  void ensure_stream_review_reviews_are_not_equal() {
    //GIVEN
    final String review = "reviews";
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setReview("reviews1");

    // ASSERT
    assertNotEquals(review, streamReview1.getReview());
  }

  //fleen stream
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
  void ensure_stream_review_fleen_stream_are_equal() {
    //GIVEN
    FleenStream fleenStream = new FleenStream();
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setFleenStream(fleenStream);

    // ASSERT
    assertNotNull(streamReview1);
    assertEquals(fleenStream, streamReview1.getFleenStream());
  }

  @Test
  void ensure_stream_fleen_stream_are_not_equal() {
    //GIVEN
    FleenStream fleenStream = new FleenStream();
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setFleenStream(new FleenStream());

    // ASSERT
    assertNotEquals(fleenStream, streamReview1.getFleenStream());
  }

  //member

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

  @Test
  void ensure_stream_review_member_are_equal() {
    //GIVEN
    Member member = new Member();
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setMember(member);

    // ASSERT
    assertNotNull(streamReview1);
    assertEquals(member, streamReview1.getMember());
  }

  @Test
  void ensure_stream_member_are_not_equal() {
    //GIVEN
    Member member = new Member();
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setMember(new Member());

    // ASSERT
    assertNotEquals(member, streamReview1.getMember());
  }

  //stream review rating
  @Test
  void create_stream_review_without_stream_review_rating() {
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

  @Test
  void ensure_stream_review_stream_review_rating_are_equal() {
    //GIVEN
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setRating(StreamReviewRating.EXCELLENT);

    // ASSERT
    assertNotNull(streamReview1);
    assertEquals(StreamReviewRating.EXCELLENT, streamReview1.getRating());
  }

  @Test
  void ensure_stream_stream_review_rating_are_not_equal() {
    //GIVEN
    final StreamReview streamReview1 = new StreamReview();
    streamReview1.setRating(StreamReviewRating.EXCELLENT);

    // ASSERT
    assertNotEquals(StreamReviewRating.POOR, streamReview1.getRating());
  }
}
