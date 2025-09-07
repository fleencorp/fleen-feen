package com.fleencorp.feen.shared.chat.space.query.constant;

public final class ChatSpaceQueryConstant {

  private ChatSpaceQueryConstant() {}

  public static final String FIND_CHAT_SPACE_BY_ID = """
    SELECT
      cs.chat_space_id          AS chatSpaceId,
      cs.external_id_or_name    AS externalIdOrName,
      cs.title                  AS title,
      cs.description            AS description,
      cs.tags                   AS tags,
      cs.guidelines_or_rules    AS guidelinesOrRules,
      cs.space_link             AS spaceLink,
      cs.member_id              AS organizerId,
      m.full_name               AS organizerName,
      cs.space_visibility       AS spaceVisibility,
      cs.space_status           AS status,
      cs.total_members          AS totalMembers,
      cs.is_deleted             AS deleted,
      cs.like_count             AS likeCount,
      cs.bookmark_count         AS bookmarkCount,
      cs.share_count            AS shareCount,
      cs.slug                   AS slug
    FROM chat_space cs
    JOIN member m ON cs.member_id = m.member_id
    WHERE cs.chat_space_id = :id
    """;
}
