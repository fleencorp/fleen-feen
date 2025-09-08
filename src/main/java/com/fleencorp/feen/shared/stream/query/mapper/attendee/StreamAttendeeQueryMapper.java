package com.fleencorp.feen.shared.stream.query.mapper.attendee;

import com.fleencorp.feen.shared.stream.model.StreamAttendeeData;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StreamAttendeeQueryMapper implements RowMapper<StreamAttendeeData> {

  private StreamAttendeeQueryMapper() {}

  public static StreamAttendeeQueryMapper of() {
    return new StreamAttendeeQueryMapper();
  }

  @Override
  public StreamAttendeeData mapRow(ResultSet rs, int rowNum) throws SQLException {
    StreamAttendeeData attendee = new StreamAttendeeData();

    attendee.setAttendeeId(rs.getLong("attendeeId"));
    attendee.setStreamId(rs.getLong("streamId"));
    attendee.setMemberId(rs.getLong("memberId"));

    final String requestToJoinStatusValue = rs.getString("requestToJoinStatus");
    final StreamAttendeeRequestToJoinStatus requestToJoinStatus = StreamAttendeeRequestToJoinStatus.of(requestToJoinStatusValue);

    attendee.setRequestToJoinStatus(requestToJoinStatus);
    attendee.setAttending(rs.getBoolean("attending"));
    attendee.setASpeaker(rs.getBoolean("aSpeaker"));
    attendee.setIsOrganizer(rs.getBoolean("isOrganizer"));

    attendee.setAttendeeComment(rs.getString("attendeeComment"));
    attendee.setOrganizerComment(rs.getString("organizerComment"));

    attendee.setEmailAddress(rs.getString("emailAddress"));
    attendee.setFullName(rs.getString("fullName"));
    attendee.setUsername(rs.getString("username"));
    attendee.setProfilePhoto(rs.getString("profilePhoto"));

    return attendee;
  }
}

