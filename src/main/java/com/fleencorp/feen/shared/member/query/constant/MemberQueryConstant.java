package com.fleencorp.feen.shared.member.query.constant;

public final class MemberQueryConstant {

  private MemberQueryConstant() {}

  public static final String FIND_MEMBER_BY_ID = """
    SELECT
      m.member_id               AS memberId,
      m.country                 AS country,
      m.username                AS username,
      m.first_name              AS firstName,
      m.last_name               AS lastName,
      CONCAT(
        m.first_name,
      CONCAT(' ',
        m.last_name))           AS fullName,
      m.password_hash           AS password,
      m.phone_number            AS phoneNumber,
      m.email_address           AS emailAddress,
      m.profile_photo_url       AS profilePhoto,
      m.mfa_enabled             AS mfaEnabled,
      m.phone_number_verified   AS phoneNumberVerified,
      m.email_address_verified  AS emailAddressVerified,
      m.mfa_type                AS mfaType,
      m.profile_status          AS profileStatus,
      m.verification_status     AS profileVerificationStatus
    FROM member m
    WHERE m.member_id = :id
    """;


}
