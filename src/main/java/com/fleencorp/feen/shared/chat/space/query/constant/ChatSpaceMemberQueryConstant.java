package com.fleencorp.feen.shared.chat.space.query.constant;

public final class ChatSpaceMemberQueryConstant {

  private ChatSpaceMemberQueryConstant() {}

  public static final String FIND_BY_CHAT_SPACE_AND_MEMBER_AND_STATUS = """
    SELECT
        csm.chat_space_member_id  AS chatSpaceMemberId,
        cs.external_id_or_name AS parentExternalIdOrName,
        cs.external_id_or_name    AS externalIdOrName,
        cs.chat_space_id          AS chatSpaceId,
        m.member_id               AS memberId,
        csm.has_left              AS hasLeft,
        csm.is_removed            AS removed,
        csm.member_comment        AS memberComment,
        csm.space_admin_comment   AS spaceAdminComment,
        m.email_address           AS emailAddress,
        CONCAT(m.first_name, ' ', m.last_name) AS fullName,
        m.username                AS username,
        m.profile_photo_url       AS profilePhoto
    FROM chat_space_member csm
    JOIN chat_space cs ON cs.chat_space_id = csm.chat_space_id
    JOIN member m ON m.member_id = csm.member_id
    WHERE csm.chat_space_id = :chatSpaceId
      AND csm.member_id = :memberId
      AND csm.request_to_join_status = :joinStatus
    """;
}

