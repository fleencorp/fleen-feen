package com.fleencorp.feen.stream.repository.core;

import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface StreamRepository extends JpaRepository<FleenStream, Long> {


  /**
   * Finds past streams that were attended by both specified users.
   *
   * <p>This query returns streams that have ended, are not deleted, and whose
   * status is in the included list. It includes only those streams where
   * both {@code userAId} and {@code userBId} were approved attendees and marked
   * as attending.</p>
   *
   * <p>The result is ordered by the stream's scheduled end date in descending
   * order and returned as a pageable result.</p>
   *
   * @param memberAId The ID of the first user.
   * @param memberBId The ID of the second user.
   * @param approvedStatuses The statuses that indicate a user was approved to join.
   * @param includedStatuses The stream statuses to include from the result.
   * @param pageable The pagination and sorting configuration.
   * @return A page of {@link FleenStream} instances attended by both users.
   */
  @Query(value =
    """
    SELECT fs FROM FleenStream fs
    WHERE fs.deleted = false
      AND fs.streamStatus IN (:includedStatuses)
      AND fs.scheduledEndDate < CURRENT_TIMESTAMP
      AND EXISTS (
        SELECT 1 FROM StreamAttendee sa1
        WHERE sa1.stream = fs
          AND sa1.memberId = :memberAId
          AND sa1.requestToJoinStatus IN (:approvedStatuses)
          AND sa1.attending = true
      )
      AND EXISTS (
        SELECT 1 FROM StreamAttendee sa2
        WHERE sa2.stream = fs
          AND sa2.memberId = :memberBId
          AND sa2.requestToJoinStatus IN (:approvedStatuses)
          AND sa2.attending = true
      )
    ORDER BY fs.scheduledEndDate DESC
    """)
  Page<FleenStream> findCommonPastAttendedStreams(
    @Param("memberAId") Long memberAId,
    @Param("memberBId") Long memberBId,
    @Param("approvedStatuses") Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses,
    @Param("includedStatuses") Collection<StreamStatus> includedStatuses,
    Pageable pageable);

  /**
   * Finds streams created by a member, including streams with certain statuses.
   *
   * <p>This query returns streams where the member with the specified
   * {@code memberId} is the creator. The streams are filtered to include
   * those not marked as deleted and any streams whose status is in the
   * {@code includedStatuses} list.</p>
   *
   * <p>The result is ordered by the scheduled start date of the stream in
   * descending order and returned as a pageable result.</p>
   *
   * @param memberId The ID of the member who created the streams.
   * @param includedStatuses The statuses of streams to include from the result.
   * @param pageable The pagination and sorting configuration.
   * @return A page of {@link FleenStream} instances created by the member,
   *         including those with the specified statuses.
   */
  @Query(value =
    """
    SELECT fs FROM FleenStream fs
    WHERE fs.memberId = :memberId
      AND fs.deleted = false
      AND fs.streamStatus IN (:includedStatuses)
    ORDER BY fs.scheduledStartDate DESC
    """)
  Page<FleenStream> findStreamsCreatedByMember(
    @Param("memberId") Long memberId,
    @Param("includedStatuses") Collection<StreamStatus> includedStatuses,
    Pageable pageable);
}
