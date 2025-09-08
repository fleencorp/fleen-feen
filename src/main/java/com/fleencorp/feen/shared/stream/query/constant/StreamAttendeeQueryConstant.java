package com.fleencorp.feen.shared.stream.query.constant;

public final class StreamAttendeeQueryConstant {

  private StreamAttendeeQueryConstant() {}

  public static final String FIND_ATTENDEE_BY_ID = """
    SELECT
      a.stream_attendee_id      AS attendeeId,
      a.stream_id               AS streamId,
      a.member_id               AS memberId,
      a.request_to_join_status  AS requestToJoinStatus,
      a.is_attending            AS attending,
      a.is_a_speaker            AS aSpeaker,
      a.is_organizer            AS isOrganizer,
      a.attendee_comment        AS attendeeComment,
      a.organizer_comment       AS organizerComment,
      m.email_address           AS emailAddress,
      CONCAT(
        m.first_name,
        ' ',
        m.last_name)            AS fullName,
      m.username                AS username,
      m.profile_photo_url       AS profilePhoto
    FROM stream_attendee a
    JOIN member m ON a.member_id = m.member_id
    WHERE a.stream_attendee_id = :id
    """;
}

