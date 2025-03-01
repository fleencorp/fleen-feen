package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.*;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.user.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fleen Stream")
class FleenStreamTest {

  @Test
  @DisplayName("Ensure stream is empty")
  void ensure_stream_is_empty() {
    // given
    final FleenStream stream = FleenStream.empty();

    // then
    assertNull(stream);
  }

  @Test
  @DisplayName("Ensure stream is not empty")
  void ensure_stream_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNotNull(stream);
  }

  @Test
  @DisplayName("Ensure id is empty")
  void ensure_id_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamId());
  }

  @Test
  @DisplayName("Ensure id is not empty")
  void ensure_id_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamId(1L);

    // then
    assertNotNull(stream.getStreamId());
  }

  @Test
  @DisplayName("Ensure external id is empty")
  void ensure_external_id_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getExternalId());
  }

  @Test
  @DisplayName("Ensure external id is not empty")
  void ensure_external_id_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setExternalId("buBeVoDdOk");

    // then
    assertNotNull(stream.getExternalId());
  }

  @Test
  @DisplayName("Ensure title is empty")
  void ensure_title_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getTitle());
  }

  @Test
  @DisplayName("Ensure title is not empty")
  void ensure_title_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setTitle("Java Conference");

    // then
    assertNotNull(stream.getTitle());
  }

  @Test
  @DisplayName("Ensure description is empty")
  void ensure_description_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getDescription());
  }

  @Test
  @DisplayName("Ensure description is not empty")
  void ensure_description_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setDescription("Java Conference for Programmers & Software Engineers");

    // then
    assertNotNull(stream.getDescription());
  }

  @Test
  @DisplayName("Ensure tags is empty")
  void ensure_tags_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getTags());
  }

  @Test
  @DisplayName("Ensure tags is not empty")
  void ensure_tags_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setTags("Coding, Programming, Tech, Web");

    // then
    assertNotNull(stream.getTags());
  }

  @Test
  @DisplayName("Ensure location is empty")
  void ensure_location_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getLocation());
  }

  @Test
  @DisplayName("Ensure location is not empty")
  void ensure_location_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setLocation("Paris");

    // then
    assertNotNull(stream.getLocation());
  }

  @Test
  @DisplayName("Ensure timezone is empty")
  void ensure_timezone_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getTimezone());
  }

  @Test
  @DisplayName("Ensure timezone is not empty")
  void ensure_timezone_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setTimezone("Europe/Paris");

    // then
    assertNotNull(stream.getTimezone());
  }

  @Test
  @DisplayName("Ensure organizer name is empty")
  void ensure_organizer_name_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOrganizerName());
  }

  @Test
  @DisplayName("Ensure organizer name is not empty")
  void ensure_organizer_name_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setOrganizerName("Jimmy Jones");

    // then
    assertNotNull(stream.getOrganizerName());
  }

  @Test
  @DisplayName("Ensure organizer email is empty")
  void ensure_organizer_email_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOrganizerEmail());
  }

  @Test
  @DisplayName("Ensure organizer email is not empty")
  void ensure_organizer_email_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setOrganizerEmail("Jimmy Jones");

    // then
    assertNotNull(stream.getOrganizerEmail());
  }

  @Test
  @DisplayName("Ensure organizer phone is empty")
  void ensure_organizer_phone_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOrganizerPhone());
  }

  @Test
  @DisplayName("Ensure organizer phone is not empty")
  void ensure_organizer_phone_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setOrganizerPhone("+2348012345678");

    // then
    assertNotNull(stream.getOrganizerPhone());
  }

  @Test
  @DisplayName("Ensure stream link is empty")
  void ensure_stream_link_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamLink());
  }

  @Test
  @DisplayName("Ensure stream link is not empty")
  void ensure_stream_link_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamLink("https://meet.google.com/zue-zvfj-iks");

    // then
    assertNotNull(stream.getStreamLink());
  }

  @Test
  @DisplayName("Ensure thumbnail link is empty")
  void ensure_thumbnail_link_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getThumbnailLink());
  }

  @Test
  @DisplayName("Ensure thumbnail link is not empty")
  void ensure_thumbnail_link_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setThumbnailLink("https://www.feenlive.com/photo.jpg");

    // then
    assertNotNull(stream.getThumbnailLink());
  }

  @Test
  @DisplayName("Ensure source is empty")
  void ensure_source_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamSource());
  }

  @Test
  @DisplayName("Ensure source is not empty")
  void ensure_source_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamSource(StreamSource.GOOGLE_MEET);

    // then
    assertNotNull(stream.getStreamSource());
  }

  @Test
  @DisplayName("Ensure type is empty")
  void ensure_type_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamType());
  }

  @Test
  @DisplayName("Ensure type is not empty")
  void ensure_type_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamType(StreamType.EVENT);

    // then
    assertNotNull(stream.getStreamType());
  }

  @Test
  @DisplayName("Ensure creation type is empty")
  void ensure_creation_type_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamCreationType());
  }

  @Test
  @DisplayName("Ensure creation type is not empty")
  void ensure_creation_type_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamCreationType(StreamCreationType.INSTANT);

    // then
    assertNotNull(stream.getStreamCreationType());
  }

  @Test
  @DisplayName("Ensure visibility is empty")
  void ensure_visibility_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamVisibility());
  }

  @Test
  @DisplayName("Ensure visibility is not empty")
  void ensure_visibility_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamVisibility(StreamVisibility.PUBLIC);

    // then
    assertNotNull(stream.getStreamVisibility());
  }

  @Test
  @DisplayName("Ensure status is empty")
  void ensure_status_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getStreamStatus());
  }

  @Test
  @DisplayName("Ensure status is not empty")
  void ensure_status_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamStatus(StreamStatus.ACTIVE);

    // then
    assertNotNull(stream.getStreamStatus());
  }

  @Test
  @DisplayName("Ensure start date is empty")
  void ensure_start_date_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getScheduledStartDate());
  }

  @Test
  @DisplayName("Ensure start date is not empty")
  void ensure_start_date_is_not_empty() {
    // given
    final LocalDateTime startDate = LocalDateTime.of(2025, 1, 1, 0,0,0);

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDate);

    // then
    assertNotNull(stream.getScheduledStartDate());
  }

  @Test
  @DisplayName("Ensure end date is empty")
  void ensure_end_date_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getScheduledEndDate());
  }

  @Test
  @DisplayName("Ensure end date is not empty")
  void ensure_end_date_is_not_empty() {
    // given
    final LocalDateTime endDate = LocalDateTime.of(2025, 1, 1, 3,0,0);

    final FleenStream stream = new FleenStream();
    stream.setScheduledEndDate(endDate);

    // then
    assertNotNull(stream.getScheduledEndDate());
  }

  @Test
  @DisplayName("Ensure other details is empty")
  void ensure_other_details_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOtherDetails());
  }

  @Test
  @DisplayName("Ensure other details is not empty")
  void ensure_other_details_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setOtherDetails("For Java Professionals & Enthusiast");

    // then
    assertNotNull(stream.getOtherDetails());
  }

  @Test
  @DisplayName("Ensure other link is empty")
  void ensure_other_link_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOtherLink());
  }

  @Test
  @DisplayName("Ensure other link is not empty")
  void ensure_other_link_is_not_empty() {
    final FleenStream stream = new FleenStream();
    stream.setOtherLink("https://www.mycompany.com");

    assertNotNull(stream.getOtherLink());
  }

  @Test
  @DisplayName("Ensure group or organization name is empty")
  void ensure_group_or_organization_name_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getGroupOrOrganizationName());
  }

  @Test
  @DisplayName("Ensure group or organization name is not empty")
  void ensure_group_or_organization_name_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setGroupOrOrganizationName("Tech & Lifestyle");

    // then
    assertNotNull(stream.getGroupOrOrganizationName());
  }

  @Test
  @DisplayName("Ensure member is empty")
  void ensure_member_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getMember());
  }

  @Test
  @DisplayName("Ensure stream member is not empty")
  void ensure_member_is_not_empty() {
    // given
    final Member member = new Member();

    final FleenStream stream = new FleenStream();
    stream.setMember(member);

    // then
    assertNotNull(stream.getMember());
  }

  @Test
  @DisplayName("Ensure attendees is set by default")
  void ensure_attendees_is_set_by_default() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNotNull(stream.getAttendees());
  }

  @Test
  @DisplayName("Ensure chat space is empty")
  void ensure_chat_space_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getChatSpace());
  }

  @Test
  @DisplayName("Ensure chat space is not empty")
  void ensure_chat_space_is_not_empty() {
    // given
    final ChatSpace chatSpace = new ChatSpace();

    final FleenStream stream = new FleenStream();
    stream.setChatSpace(chatSpace);

    // then
    assertNotNull(stream.getChatSpace());
  }

  @Test
  @DisplayName("Ensure made for kids is set by default")
  void ensure_for_kids_is_set_by_default() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNotNull(stream.getForKids());
  }

  @Test
  @DisplayName("Ensure deleted is set by default")
  void ensure_deleted_is_set_by_default() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNotNull(stream.getDeleted());
  }

  @Test
  @DisplayName("Ensure total attendees is set by default")
  void ensure_total_attendees_is_set_by_default() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNotNull(stream.getTotalAttendees());
  }

  @Test
  @DisplayName("Ensure for kids is false by default")
  void ensure_for_kids_is_false() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.getForKids());
  }

  @Test
  @DisplayName("Ensure deleted is false by default")
  void ensure_deleted_is_false() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.getDeleted());
  }

  @Test
  @DisplayName("Ensure is for kids is not empty")
  void ensure_for_kids_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertTrue(stream.isForKids());
  }

  @Test
  @DisplayName("Ensure chat space id is empty")
  void ensure_chat_space_id_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getChatSpaceId());
  }

  @Test
  @DisplayName("Ensure chat space id is not empty")
  void ensure_chat_space_id_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setChatSpaceId(1L);

    // then
    assertNotNull(stream.getChatSpaceId());
  }

  @Test
  @DisplayName("Ensure chat space external id or name is empty")
  void ensure_chat_space_external_id_or_name_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getExternalSpaceIdOrName());
  }

  @Test
  @DisplayName("Ensure chat space external id or name is not empty")
  void ensure_chat_space_external_id_or_name_is_not_empty() {
    // given
    final ChatSpace chatSpace = new ChatSpace();
    chatSpace.setExternalIdOrName("AaAbVH8QN6o");

    final FleenStream stream = new FleenStream();
    stream.setChatSpace(chatSpace);

    // then
    assertNotNull(stream.getExternalSpaceIdOrName());
  }

  @Test
  @DisplayName("Ensure organizer is empty")
  void ensure_organizer_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOrganizer());
  }

  @Test
  @DisplayName("Ensure organizer is not empty")
  void ensure_organizer_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    final Member member = new Member();
    stream.setMember(member);

    // then
    assertNotNull(stream.getOrganizer());
  }

  @Test
  @DisplayName("Ensure update of title, description, tags and location")
  void ensure_update_of_title_description_tags_and_location() {
    // given
    final FleenStream stream = new FleenStream();
    final String title = "Python";
    final String description = "Java Conference for Programmers & Software Engineers";
    final String tags = "Coding, Programming, Tech, Web, AI";
    final String location = "Monaco";

    // act
    stream.update(title, description, tags, location);

    // assert
    assertEquals(title, stream.getTitle());
    assertEquals(description, stream.getDescription());
    assertEquals(tags, stream.getTags());
    assertEquals(location, stream.getLocation());
  }

  @Test
  @DisplayName("Ensure update of external id and link")
  void ensure_update_of_external_id_and_link() {
    // given
    final FleenStream stream = new FleenStream();
    final String externalId = "buBeVoDdOd";
    final String link = "https://meet.google.com/zue-ghfs-iks";

    // act
    stream.update(externalId, link);

    // then
    assertEquals(externalId, stream.getExternalId());
    assertEquals(link, stream.getStreamLink());
  }

  @Test
  @DisplayName("Ensure update of organizer name, email and phone")
  void ensure_update_of_organizer_name_and_email_and_phone() {
    // given
    final FleenStream stream = new FleenStream();
    final String name = "Jimmy Jones";
    final String email = "jimmy@example.com";
    final String phone = "+2348023456789";

    // act
    stream.update(name, email, phone);

    // then
    assertEquals(name, stream.getOrganizerName());
    assertEquals(email, stream.getOrganizerEmail());
    assertEquals(phone, stream.getOrganizerPhone());
  }

  @Test
  @DisplayName("Ensure reschedule by start date, end date and timezone")
  void ensure_update_by_start_date_and_timezone() {
    // given
    final FleenStream stream = new FleenStream();
    final LocalDateTime startDateTime = LocalDateTime.now();
    final LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);
    final String timezone = "Europe/Monaco";

    // act
    stream.reschedule(startDateTime, endDateTime, timezone);

    // then
    assertEquals(startDateTime, stream.getScheduledStartDate());
    assertEquals(endDateTime, stream.getScheduledEndDate());
    assertEquals(timezone, stream.getTimezone());
  }

  @Test
  @DisplayName("Ensure it is marked as deleted")
  void ensure_stream_is_marked_as_deleted() {
    // given
    final FleenStream stream = new FleenStream();

    // act
    stream.delete();

    // then
    assertTrue(stream.isDeleted());
  }

  @Test
  @DisplayName("Ensure it is marked as canceled")
  void ensure_stream_is_marked_as_canceled() {
    // given
    final FleenStream stream = new FleenStream();

    // act
    stream.cancel();

    // then
    assertTrue(stream.isCanceled());
  }

  @Test
  @DisplayName("Ensure it is not private or protected")
  void ensure_stream_is_not_private_or_protected() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.isPrivateOrProtected());
  }

  @Test
  @DisplayName("Ensure it is not public")
  void ensure_stream_is_not_public() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.isPublic());
  }

  @Test
  @DisplayName("Ensure it is not private")
  void ensure_stream_is_not_private() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.isPrivate());
  }

  @Test
  @DisplayName("Ensure it is not canceled")
  void ensure_stream_is_not_canceled() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.isCanceled());
  }

  @Test
  @DisplayName("Ensure it is not ongoing")
  void ensure_stream_is_not_ongoing() {
    // given
    final LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);
    final LocalDateTime endDateTime = LocalDateTime.now().plusHours(2);

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDateTime);
    stream.setScheduledEndDate(endDateTime);


    // then
    assertFalse(stream.isOngoing());
  }

  @Test
  @DisplayName("Ensure it is ongoing")
  void ensure_stream_is_ongoing() {
    // given
    final LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(30);
    final LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDateTime);
    stream.setScheduledEndDate(endDateTime);


    // then
    assertTrue(stream.isOngoing());
  }

  @Test
  @DisplayName("Ensure it has not ended")
  void ensure_stream_has_not_ended() {
    final LocalDateTime endDateTime = LocalDateTime.now().plusHours(1);

    final FleenStream stream = new FleenStream();
    stream.setScheduledEndDate(endDateTime);

    // then
    assertFalse(stream.hasEnded());
  }

  @Test
  @DisplayName("Ensure it has ended")
  void ensure_stream_has_ended() {
    final LocalDateTime endDateTime = LocalDateTime.now().minusHours(1);

    final FleenStream stream = new FleenStream();
    stream.setScheduledEndDate(endDateTime);

    // then
    assertTrue(stream.hasEnded());
  }

  @Test
  @DisplayName("Ensure organizer id is empty")
  void ensure_organizer_id_is_empty() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getOrganizerId());
  }

  @Test
  @DisplayName("Ensure organizer id is not empty")
  void ensure_organizer_id_is_not_empty() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setMemberId(1L);

    // then
    assertNotNull(stream.getOrganizerId());
  }

  @Test
  @DisplayName("Ensure it is not an event")
  void ensure_it_is_not_an_event() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.isAnEvent());
  }

  @Test
  @DisplayName("Ensure it is an event")
  void ensure_it_is_an_event() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamType(StreamType.EVENT);

    // then
    assertTrue(stream.isAnEvent());
  }

  @Test
  @DisplayName("Ensure it is not a live stream")
  void ensure_it_is_not_a_live_stream() {
    // given
    final FleenStream stream = new FleenStream();

    // them
    assertFalse(stream.isALiveStream());
  }

  @Test
  @DisplayName("Ensure it is a live stream")
  void ensure_it_is_a_live_stream() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamType(StreamType.LIVE_STREAM);

    // then
    assertTrue(stream.isALiveStream());
  }

  @Test
  @DisplayName("Ensure it has no chat space id")
  void ensure_it_has_no_chat_space_id() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertFalse(stream.hasChatSpaceId());
  }

  @Test
  @DisplayName("Ensure it has a chat space id")
  void ensure_it_has_a_chat_space_id() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setChatSpaceId(1L);

    // then
    assertTrue(stream.hasChatSpaceId());
  }

  @Test
  @DisplayName("Ensure increase of total attendees")
  void ensure_increase_of_total_attendees() {
    // given
    final FleenStream stream = new FleenStream();

    // act
    stream.increaseTotalAttendees();

    // then
    assertEquals(1, stream.getTotalAttendees());
  }

  @Test
  @DisplayName("Ensure decrease of total attendees")
  void ensure_decrease_of_total_attendees() {
    // given
    final FleenStream stream = new FleenStream();

    // act
    stream.decreaseTotalAttendees();

    // then
    assertEquals(0, stream.getTotalAttendees());
  }

  @Test
  @DisplayName("Ensure stream time type is live")
  void ensure_stream_time_type_is_live() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(LocalDateTime.now());

    // then
    assertEquals(StreamTimeType.PAST, stream.getStreamSchedule());
  }

  @Test
  @DisplayName("Ensure stream time type is upcoming if no start date")
  void ensure_stream_time_type_is_upcoming_if_no_start_date() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertEquals(StreamTimeType.UPCOMING, stream.getStreamSchedule(LocalDateTime.now()));
  }

  @Test
  @DisplayName("Ensure stream time type is upcoming")
  void ensure_stream_time_type_is_upcoming() {
    // given
    final LocalDateTime startDateTime = LocalDateTime.now().plusHours(1);

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDateTime);

    // then
    assertEquals(StreamTimeType.UPCOMING, stream.getStreamSchedule(LocalDateTime.now()));
  }

  @Test
  @DisplayName("Ensure stream time type is past")
  void ensure_stream_time_type_is_past() {
    // given
    final LocalDateTime startDateTime = LocalDateTime.now().minusHours(1);

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDateTime);

    // then
    assertEquals(StreamTimeType.PAST, stream.getStreamSchedule(LocalDateTime.now()));
  }

  @Test
  @DisplayName("Ensure stream time type is live")
  void ensure_stream_time_type_is_live_by_default() {
    // given
    final LocalDateTime startDateTime = LocalDateTime.now();

    final FleenStream stream = new FleenStream();
    stream.setScheduledStartDate(startDateTime);

    // then
    assertEquals(StreamTimeType.LIVE, stream.getStreamSchedule(startDateTime));
  }

  @Test
  @DisplayName("Ensure stream type not equal")
  void ensure_stream_type_not_equal() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamType(StreamType.EVENT);

    // act and then
    assertThrows(FailedOperationException.class, () -> stream.checkStreamTypeNotEqual(StreamType.LIVE_STREAM));
  }

  @Test
  @DisplayName("Ensure stream link is not masked")
  void ensure_stream_link_is_not_masked() {
    // given
    final FleenStream stream = new FleenStream();

    // then
    assertNull(stream.getMaskedStreamLink());
  }

  @Test
  @DisplayName("Ensure stream link is masked")
  void ensure_stream_link_masked() {
    // given
    final FleenStream stream = new FleenStream();
    stream.setStreamLink("https://meet.google.com/zue-zvfj-iks");

    // then
    assertNotNull(stream.getMaskedStreamLink());
  }
}