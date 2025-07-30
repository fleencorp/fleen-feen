package com.fleencorp.feen.stream.repository.attendee;

import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.domain.StreamAttendee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface StreamAttendeeRepository extends JpaRepository<StreamAttendee, Long> {

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.stream.streamType = :streamType")
  Page<StreamAttendee> findByStreamAndStreamType(FleenStream stream, StreamType streamType, Pageable pageable);

  @Query(value = "SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.requestToJoinStatus IN (:requestToJoinStatuses)")
  Page<StreamAttendee> findByStreamAndRequestToJoinStatus(FleenStream stream, @Param("requestToJoinStatuses") Set<StreamAttendeeRequestToJoinStatus> requestToJoinStatuses, Pageable pageable);

  @Query("SELECT sa FROM StreamAttendee sa WHERE sa.stream = :stream AND sa.attending = true")
  Page<StreamAttendee> findAttendeesGoingToStream(@Param("stream") FleenStream stream, Pageable pageable);

  Page<StreamAttendee> findAllByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending, Pageable pageable);
}
