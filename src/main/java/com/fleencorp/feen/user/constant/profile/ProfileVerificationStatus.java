package com.fleencorp.feen.user.constant.profile;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum ProfileVerificationStatus {

  APPROVED("Approved", "profile.verification.status.approved"),
  DISAPPROVED("Disapproved", "profile.verification.status.disapproved"),
  IN_PROGRESS("In Progress", "profile.verification.status.in.progress"),
  PENDING("Pending", "profile.verification.status.pending"),;

  private final String label;
  private final String messageCode;

  ProfileVerificationStatus(
      final String label,
      final String messageCode) {
    this.label = label;
    this.messageCode = messageCode;
  }

  public static ProfileVerificationStatus of(final String value) {
    return parseEnumOrNull(value, ProfileVerificationStatus.class);
  }

  /**
   * Determines whether the given profile verification status is approved.
   *
   * @param profileVerificationStatus the status of the profile verification
   * @return {@code true} if the status is approved; {@code false} otherwise
   */
  public static boolean isApproved(final ProfileVerificationStatus profileVerificationStatus) {
    return APPROVED == profileVerificationStatus;
  }

}
