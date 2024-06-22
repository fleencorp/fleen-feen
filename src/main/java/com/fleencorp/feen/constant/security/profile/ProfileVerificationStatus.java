package com.fleencorp.feen.constant.security.profile;

import com.fleencorp.feen.constant.base.ApiParameter;
import lombok.Getter;

/**
 * Enumeration for Profile Verification Status types.
 *
 * <p>This enum defines the various verification statuses a user profile can have.
 * Each enum constant is associated with a string value that represents the verification status.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ProfileVerificationStatus implements ApiParameter {

  PENDING("Pending"),
  IN_PROGRESS("In Progress"),
  DISAPPROVED("Disapproved"),
  APPROVED("Approved");

  private final String value;

  ProfileVerificationStatus(String value) {
    this.value = value;
  }
}
