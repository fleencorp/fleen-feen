package com.fleencorp.feen.repository.stream.attendee;

import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface StreamAttendeeManagementRepository extends JpaRepository<StreamAttendee, Long> {

  int countByStreamAndRequestToJoinStatusAndAttending(FleenStream stream, StreamAttendeeRequestToJoinStatus requestToJoinStatus, Boolean isAttending);

  @Query("SELECT COUNT(sa.attendeeId) FROM StreamAttendee sa WHERE sa.attendeeId IN (:ids)")
  long countByIds(@Param("ids") Set<Long> ids);

  @Modifying
  @Query("UPDATE StreamAttendee SET requestToJoinStatus = :status WHERE attendeeId IN (:attendeeIds)")
  void approveAllAttendeeRequestInvitation(@Param("status") StreamAttendeeRequestToJoinStatus status, List<Long> attendeeIds);

  @Modifying
  @Query("UPDATE StreamAttendee SET aSpeaker = true WHERE attendeeId IN (:attendeeIds)")
  void markAllAttendeesAsSpeaker(List<Long> attendeeIds);
}
