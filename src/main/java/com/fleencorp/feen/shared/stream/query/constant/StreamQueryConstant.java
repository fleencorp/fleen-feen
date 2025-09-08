package com.fleencorp.feen.shared.stream.query.constant;

public final class StreamQueryConstant {

  private StreamQueryConstant() {}

  public static final String FIND_STREAM_BY_ID = """
    SELECT
      s.stream_id              AS streamId,
      s.external_id            AS externalId,
      s.chat_space_id          AS chatSpaceId,
      s.title                  AS title,
      s.description           AS description,
      s.tags                  AS tags,
      s.location              AS location,
      s.total_speakers        AS totalSpeakers,
      s.total_attendees       AS totalAttendees,
      s.bookmark_count        AS bookmarkCount,
      s.like_count            AS likeCount,
      s.share_count           AS shareCount,
      s.timezone              AS timezone,
      s.scheduled_start_date  AS scheduledStartDate,
      s.scheduled_end_date    AS scheduledEndDate,
      s.stream_link           AS streamLink,
      s.thumbnail_link        AS thumbnailLink,
      s.other_details         AS otherDetails,
      s.other_link            AS otherLink,
      s.group_or_organization_name AS groupOrOrganizationName,
      s.music_link            AS musicLink,
      s.source                AS streamSource,
      s.type                  AS streamType,
      s.creation_type         AS streamCreationType,
      s.visibility            AS streamVisibility,
      s.status                AS streamStatus,
      s.organizer_name        AS organizerName,
      s.organizer_email       AS organizerEmail,
      s.organizer_phone       AS organizerPhone,
      s.member_id             AS memberId,
      s.is_deleted            AS deleted,
      s.made_for_kids         AS forKids,
      s.slug                  AS slug,
      s.created_on            AS createdOn,
      s.updated_on            AS updatedOn
    FROM stream s
    WHERE s.stream_id = :id
    """;

  public static final String FIND_STREAM_ID_AND_TITLE_BY_ID = """
    SELECT
      s.stream_id AS streamId,
      s.title     AS title
    FROM stream s
    WHERE s.stream_id = :id
    """;

  public static final String FIND_STREAM_CHAT_SPACE_BY_ID = """
    SELECT
      s.stream_id           AS streamId,
      s.title               AS title,
      c.external_id_or_name AS externalSpaceIdOrName
    FROM stream s
    JOIN chat_space c ON s.chat_space_id = c.chat_space_id
    WHERE s.stream_id = :id
    """;

  public static final String FIND_COMMON_PAST_ATTENDED_STREAMS = """
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
  """;

  public static final String FIND_STREAMS_CREATED_BY_MEMBER = """
        SELECT
            fs.stream_id                          AS streamId,
            fs.external_id                        AS externalId,
            fs.chat_space_id                      AS chatSpaceId,
            fs.title                              AS title,
            fs.description                        AS description,
            fs.tags                               AS tags,
            fs.location                           AS location,
            fs.total_speakers                     AS totalSpeakers,
            fs.total_attendees                    AS totalAttendees,
            fs.bookmark_count                     AS bookmarkCount,
            fs.like_count                         AS likeCount,
            fs.share_count                        AS shareCount,
            fs.timezone                           AS timezone,
            fs.scheduled_start_date               AS scheduledStartDate,
            fs.scheduled_end_date                 AS scheduledEndDate,
            fs.stream_link                        AS maskedStreamLink,
            fs.other_details                      AS otherDetails,
            fs.other_link                         AS otherLink,
            fs.group_or_organization_name         AS groupOrOrganizationName,
            fs.music_link                         AS musicLink,
            fs.stream_source                      AS streamSource,
            fs.stream_creation_type               AS streamSchedule,
            fs.stream_visibility                  AS streamVisibility,
            fs.stream_status                      AS streamStatus,
            fs.stream_type                        AS streamType,
            fs.is_deleted                         AS deleted,
            fs.made_for_kids                      AS forKids,
            fs.organizer_name                     AS organizerName,
            fs.organizer_email                    AS organizerEmail,
            fs.organizer_phone                    AS organizerPhone,
            fs.member_id                          AS memberId,
            fs.stream_link                        AS streamLink,
            fs.created_on                         AS createdOn,
            fs.updated_on                         AS updatedOn,
            fs.slug                               AS slug,
            cs.external_id_or_name                AS externalSpaceIdOrName
        FROM stream fs
        JOIN chat_space cs ON fs.chat_space_id = cs.chat_space_id
        WHERE fs.member_id = :memberId
          AND fs.is_deleted = FALSE
          AND fs.stream_status = ANY(:includedStatuses)
        ORDER BY fs.scheduled_start_date DESC
        """;

}
