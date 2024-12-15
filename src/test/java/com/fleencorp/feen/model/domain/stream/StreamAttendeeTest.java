package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StreamAttendeeTest {

  @DisplayName("Create empty StreamAttendee")
  @Test
  void create_empty_stream_attendee() {
    // GIVEN
    final StreamAttendee streamAttendee = new StreamAttendee();

    // ASSERT
    assertNotNull(streamAttendee);
  }

  @DisplayName("Create a null StreamAttendee")
  @Test
  void create_null_stream_attendee(){
    // GIVEN
    final StreamAttendee streamAttendee = null;

    // ASSERT
    assertNull(streamAttendee);
  }

  @DisplayName("Create a StreamAttendee without id")
  @Test
  void create_stream_attendee_without_id(){
    // GIVEN
    final StreamAttendee streamAttendee = new StreamAttendee();

    // ASSERT
    assertNull(streamAttendee.getStreamAttendeeId());
  }

  @DisplayName("Create a StreamAttendee without id")
  @Test
  void create_stream_attendee_with_id(){
    // GIVEN
    final StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setStreamAttendeeId(1L);

    // ASSERT
    assertNotNull(streamAttendee.getStreamAttendeeId());
  }

  @DisplayName("Ensure StreamAttendee Id is not null")
  @Test
  void ensure_stream_attendee_id_is_not_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setStreamAttendeeId(1L);
//    ASSERT
    assertNotNull(streamAttendee.getStreamAttendeeId());
  }

  @DisplayName("Ensure StreamAttendee Id is null")
  @Test
  void ensure_stream_attendee_id_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
//    ASSERT
    assertNull(streamAttendee.getStreamAttendeeId());
  }

  @DisplayName("Ensure StreamAttendee Ids are equal")
  @Test
  void ensure_stream_attendee_ids_are_equal() {
//    GIVEN

    Long streamAttendeeId = 1L;
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setStreamAttendeeId(1L);

//    ASSERT
    assertEquals(streamAttendee.getStreamAttendeeId(), streamAttendeeId);
  }

  @DisplayName("Ensure StreamAttendee Ids are not equal")
  @Test
  void ensure_stream_attendee_ids_are_not_equal() {
//    GIVEN

    Long streamAttendeeId = 1L;
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setStreamAttendeeId(2L);

//    ASSERT
    assertNotEquals(streamAttendee.getMemberId(), streamAttendeeId);
  }

  @DisplayName("Ensure fleenStream is not null")
  @Test
  void ensure_fleen_stream_is_not_null() {
//    GIVEN
    FleenStream fleenStream = new FleenStream();
    fleenStream.setFleenStreamId(1L);

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setFleenStream(fleenStream);
//    ASSERT
    assertNotNull(streamAttendee.getFleenStream());
  }

  @DisplayName("Ensure fleenStream is null")
  @Test
  void ensure_fleen_stream_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();

//    ASSERT
    assertNull(streamAttendee.getFleenStream());
  }

  @DisplayName("Ensure fleenStreams are equal")
  @Test
  void ensure_fleen_streams_are_equal() {
//    GIVEN
    FleenStream fleenStream = new FleenStream();

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setFleenStream(fleenStream);

//    ASSERT
    assertEquals(streamAttendee.getFleenStream(), fleenStream);
  }

  @DisplayName("Ensure fleenStreams are not equal")
  @Test
  void ensure_fleen_streams_are_not_equal() {
//    GIVEN
    FleenStream fleenStream = new FleenStream();
    fleenStream.setFleenStreamId(1L);

    FleenStream fleenStream1 = new FleenStream();
    fleenStream.setFleenStreamId(2L);

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setFleenStream(fleenStream1);

//    ASSERT
    assertNotEquals(streamAttendee.getFleenStream(), fleenStream);
  }


  @DisplayName("Ensure member is not null")
  @Test
  void ensure_member_is_not_null() {
//    GIVEN
    Member member = new Member();
    member.setMemberId(1L);

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setMember(member);
//    ASSERT
    assertNotNull(streamAttendee.getMember());
  }

  @DisplayName("Ensure member is null")
  @Test
  void ensure_member_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();

//    ASSERT
    assertNull(streamAttendee.getMember());
  }

  @DisplayName("Ensure members are equal")
  @Test
  void ensure_members_are_equal() {
//    GIVEN
    Member member = new Member();

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setMember(member);

//    ASSERT
    assertEquals(streamAttendee.getMember(), member);
  }

  @DisplayName("Ensure members are not equal")
  @Test
  void ensure_members_are_not_equal() {
//    GIVEN
    Member member = new Member();
    member.setMemberId(1L);

    Member member1 = new Member();
    member1.setMemberId(2L);

    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setMember(member1);

//    ASSERT
    assertNotEquals(streamAttendee.getMember(), member);
  }


  @DisplayName("Ensure request to join status is not null")
  @Test
  void ensure_request_to_join_status_is_not_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setRequestToJoinStatus(StreamAttendeeRequestToJoinStatus.PENDING);

//    ASSERT
    assertNotNull(streamAttendee.getRequestToJoinStatus());
  }

  @DisplayName("Ensure request to join status is null")
  @Test
  void ensure_stream_attendee_request_to_join_status_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();

//    ASSERT
    assertNull(streamAttendee.getRequestToJoinStatus());
  }


  @DisplayName("Ensure isAttending is not null")
  @Test
  void ensure_is_attending_is_not_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setIsAttending(false);

//    ASSERT
    assertNotNull(streamAttendee.getIsAttending());
  }

  @DisplayName("Ensure isAttending is null")
  @Test
  void ensure_is_attending_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setIsAttending(null);

//    ASSERT
    assertNull(streamAttendee.getIsAttending());
  }

  @DisplayName("Ensure isAttending is false")
  @Test
  void ensure_is_attending_is_false(){
    //GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setIsAttending(false);

    //ASSERT
    assertFalse(streamAttendee.isAttending());
  }

  @DisplayName("Ensure isAttending is true")
  @Test
  void ensure_is_attending_is_true(){
    //GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setIsAttending(true);

    //ASSERT
    assertTrue(streamAttendee.isAttending());
  }

  @DisplayName("Ensure attendee comment is null")
  @Test
  void ensure_attendee_comment_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();

//    ASSERT
    assertNull(streamAttendee.getAttendeeComment());
  }

  @DisplayName("Ensure attendee comment is not null")
  @Test
  void ensure_attendee_comment_is_not_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setAttendeeComment("hello");

//    ASSERT
    assertNotNull(streamAttendee.getAttendeeComment());
  }

  @DisplayName("Ensure organizer comment is null")
  @Test
  void ensure_organizer_comment_is_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();

//    ASSERT
    assertNull(streamAttendee.getOrganizerComment());
  }

  @DisplayName("Ensure organizer comment is not null")
  @Test
  void ensure_organizer_comment_is_not_null() {
//    GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setOrganizerComment("hello");

//    ASSERT
    assertNotNull(streamAttendee.getOrganizerComment());
  }

  @DisplayName("Ensure request to join status is pending")
  @Test
  void ensure_request_to_join_is_pending(){
    //GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setRequestToJoinStatus(StreamAttendeeRequestToJoinStatus.PENDING);

    //ASSERT
    assertEquals(streamAttendee.getRequestToJoinStatus(), StreamAttendeeRequestToJoinStatus.PENDING);

  }

  @DisplayName("Ensure request to join is approved")
  @Test
  void ensure_request_to_join_is_approved(){
    //GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setRequestToJoinStatus(StreamAttendeeRequestToJoinStatus.APPROVED);

    //ASSERT
    assertTrue(streamAttendee.isRequestToJoinApproved());
  }

  @DisplayName("Ensure request to join is disapproved")
  @Test
  void ensure_request_to_join_is_disapproved(){
    //GIVEN
    StreamAttendee streamAttendee = new StreamAttendee();
    streamAttendee.setRequestToJoinStatus(StreamAttendeeRequestToJoinStatus.DISAPPROVED);

    //ASSERT
    assertTrue(streamAttendee.isRequestToJoinDisapproved());
  }

}
