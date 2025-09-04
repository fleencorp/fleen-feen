package com.fleencorp.feen.shared.stream.service.impl;

import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.shared.stream.service.StreamQueryService;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service("sharedStreamQueryService")
public class StreamQueryServiceImpl implements StreamQueryService {

  private final EntityManager entityManager;

  public StreamQueryServiceImpl(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Optional<IsAStream> findStreamById(Long streamId) {
    List<IsAStream> results = entityManager.createQuery(
        "SELECT s.streamId AS streamId, s.title AS title FROM FleenStream s WHERE s.streamId = :id",
        IsAStream.class)
      .setParameter("id", streamId)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  public Optional<IsAStream> findStreamChatSpaceById(Long streamId) {
    List<IsAStream> results = entityManager.createQuery(
        """
        SELECT s.streamId AS streamId, s.title AS title, c.externalIdOrName AS getExternalSpaceIdOrName
        FROM FleenStream s
        JOIN s.chatSpace c on s.chatSpaceId = c.id
        WHERE s.streamId = :id
        """,
        IsAStream.class)
      .setParameter("id", streamId)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<IsAttendee> findAttendeeById(Long attendeeId) {
    List<IsAttendee> results = entityManager.createNativeQuery(
        """
        SELECT
          a.stream_attendee_id AS attendeeId,
          a.stream_id AS streamId,
          a.member_id AS memberId,
          a.request_to_join_status AS requestToJoinStatus,
          a.is_attending AS attending,
          a.is_a_speaker AS aSpeaker,
          a.is_organizer AS isOrganizer,
          a.attendee_comment AS attendeeComment,
          a.organizer_comment AS organizerComment,
          m.email_address AS emailAddress,
          CONCAT(m.first_name, ' ', m.last_name) AS fullName,
          m.username AS username,
          m.profile_photo_url AS profilePhoto
        FROM stream_attendee a
        JOIN member m ON a.member_id = m.member_id
        WHERE a.stream_attendee_id = :id
        """,
        IsAttendee.class)
      .setParameter("id", attendeeId)
      .getResultList();

    return results.stream().findFirst();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Page<IsAStream> findCommonPastAttendedStreams(
    Long memberAId,
    Long memberBId,
    Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable
  ) {
    List<IsAStream> results = entityManager.createNativeQuery(
        """
        SELECT 
            fs.stream_id AS streamId,
            fs.external_id AS externalId,
            fs.chat_space_id AS chatSpaceId,
            fs.title AS title,
            fs.description AS description,
            fs.tags AS tags,
            fs.location AS location,
            fs.total_speakers AS totalSpeakers,
            fs.total_attendees AS totalAttendees,
            fs.bookmark_count AS bookmarkCount,
            fs.like_count AS likeCount,
            fs.share_count AS shareCount,
            fs.timezone AS timezone,
            fs.scheduled_start_date AS scheduledStartDate,
            fs.scheduled_end_date AS scheduledEndDate,
            fs.stream_link AS streamLink,
            fs.other_details AS otherDetails,
            fs.other_link AS otherLink,
            fs.group_or_organization_name AS groupOrOrganizationName,
            fs.music_link AS musicLink,
            fs.stream_source AS streamSource,
            fs.stream_creation_type AS streamSchedule,
            fs.stream_visibility AS streamVisibility,
            fs.stream_status AS streamStatus,
            fs.stream_type AS streamType,
            fs.is_deleted AS deleted,
            fs.made_for_kids AS forKids,
            fs.organizer_name AS organizerName,
            fs.organizer_email AS organizerEmail,
            fs.organizer_phone AS organizerPhone,
            fs.member_id AS memberId,
            fs.stream_link AS streamLink,
            fs.created_on AS createdOn,
            fs.updated_on AS updatedOn,
            fs.slug AS slug,
            cs.external_id_or_name AS externalSpaceIdOrName
        FROM stream fs
        JOIN chat_space cs ON fs.chat_space_id = cs.chat_space_id
        WHERE fs.is_deleted = FALSE
          AND fs.stream_status = ANY(:includedStatuses)
          AND fs.scheduled_end_date < CURRENT_TIMESTAMP
          AND EXISTS (
              SELECT 1 
              FROM stream_attendee sa1
              WHERE sa1.stream_id = fs.stream_id
                AND sa1.member_id = :memberAId
                AND sa1.request_to_join_status = ANY(:approvedStatuses)
                AND sa1.is_attending = TRUE
          )
          AND EXISTS (
              SELECT 1
              FROM stream_attendee sa2
              WHERE sa2.stream_id = fs.stream_id
                AND sa2.member_id = :memberBId
                AND sa2.request_to_join_status = ANY(:approvedStatuses)
                AND sa2.is_attending = TRUE
          )
        ORDER BY fs.scheduled_end_date DESC
        """,
        IsAStream.class
      )
      .setParameter("memberAId", memberAId)
      .setParameter("memberBId", memberBId)
      .setParameter("approvedStatuses", approvedStatuses)
      .setParameter("includedStatuses", includedStatuses)
      .setFirstResult((int) pageable.getOffset())
      .setMaxResults(pageable.getPageSize())
      .getResultList();

    long total = results.size();

    return new PageImpl<>(results, pageable, total);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Page<IsAStream> findStreamsCreatedByMember(
    Long memberId,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable
  ) {
    List<IsAStream> results = entityManager.createNativeQuery(
        """
        SELECT 
            fs.stream_id AS streamId,
            fs.external_id AS externalId,
            fs.chat_space_id AS chatSpaceId,
            fs.title AS title,
            fs.description AS description,
            fs.tags AS tags,
            fs.location AS location,
            fs.total_speakers AS totalSpeakers,
            fs.total_attendees AS totalAttendees,
            fs.bookmark_count AS bookmarkCount,
            fs.like_count AS likeCount,
            fs.share_count AS shareCount,
            fs.timezone AS timezone,
            fs.scheduled_start_date AS scheduledStartDate,
            fs.scheduled_end_date AS scheduledEndDate,
            fs.stream_link AS maskedStreamLink,
            fs.other_details AS otherDetails,
            fs.other_link AS otherLink,
            fs.group_or_organization_name AS groupOrOrganizationName,
            fs.music_link AS musicLink,
            fs.stream_source AS streamSource,
            fs.stream_creation_type AS streamSchedule,
            fs.stream_visibility AS streamVisibility,
            fs.stream_status AS streamStatus,
            fs.stream_type AS streamType,
            fs.is_deleted AS deleted,
            fs.made_for_kids AS forKids,
            fs.organizer_name AS organizerName,
            fs.organizer_email AS organizerEmail,
            fs.organizer_phone AS organizerPhone,
            fs.member_id AS memberId,
            fs.stream_link AS streamLink,
            fs.created_on AS createdOn,
            fs.updated_on AS updatedOn,
            fs.slug AS slug,
            cs.external_id_or_name AS externalSpaceIdOrName
        FROM stream fs
        JOIN chat_space cs ON fs.chat_space_id = cs.chat_space_id
        WHERE fs.member_id = :memberId
          AND fs.is_deleted = FALSE
          AND fs.stream_status = ANY(:includedStatuses)
        ORDER BY fs.scheduled_start_date DESC
        """,
        IsAStream.class
      )
      .setParameter("memberId", memberId)
      .setParameter("includedStatuses", includedStatuses)
      .setFirstResult((int) pageable.getOffset())
      .setMaxResults(pageable.getPageSize())
      .getResultList();

    long total = results.size();

    return new PageImpl<>(results, pageable, total);
  }


}
