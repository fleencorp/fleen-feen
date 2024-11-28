package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.isNull;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "profile_token")
public class ProfileToken extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "profile_token_id", nullable = false, updatable = false, unique = true)
  private Long profileTokenId;

  @Column(name = "reset_password_token", length = 500)
  private String resetPasswordToken;

  @Column(name = "reset_password_token_expiry_date")
  private LocalDateTime resetPasswordTokenExpiryDate;

  @CreatedBy
  @OneToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false, unique = true)
  private Member member;

  /**
   * Updates the member, reset password token, and its expiry date.
   *
   * <p>This method sets the specified member, assigns a new reset password token,
   * and updates the reset password token's expiry date to the provided value.</p>
   *
   * @param member the member whose details are being updated
   * @param resetPasswordToken the new reset password token to be assigned
   * @param resetPasswordTokenExpiryDate the new expiry date for the reset password token
   */
  public void updateMemberAndResetPasswordTokenAndExpiryDate(final Member member, final String resetPasswordToken, final LocalDateTime resetPasswordTokenExpiryDate) {
    this.member = member;
    this.resetPasswordToken = resetPasswordToken;
    this.resetPasswordTokenExpiryDate = resetPasswordTokenExpiryDate;
  }

  /**
   * Resets the reset password token and its expiry date.
   *
   * <p>This method clears the reset password token and its associated expiry date
   * by setting them to {@code null}, effectively invalidating any existing reset password token.</p>
   */
  public void resetTokenAndExpiryDate() {
    this.resetPasswordToken = null;
    this.resetPasswordTokenExpiryDate = null;
  }

  /**
   * Checks if the provided verification token or code is invalid.
   *
   * <p>This method compares the given {@code verificationTokenOrCode} with the current reset
   * password token. It returns {@code true} if the reset password token is either null or
   * does not match the provided token/code (case-insensitive comparison).</p>
   *
   * @param verificationTokenOrCode the verification token or code to be validated
   * @return {@code true} if the reset password token is invalid, otherwise {@code false}
   */
  public boolean isResetPasswordTokenInValid(final String verificationTokenOrCode) {
    return isNull(resetPasswordToken) || !resetPasswordToken.equalsIgnoreCase(verificationTokenOrCode);
  }

  /**
   * Checks if the reset password token has expired.
   *
   * <p>This method checks whether the reset password token's expiry date is either null
   * or has passed the current date and time. It returns {@code true} if the token is expired,
   * otherwise {@code false}.</p>
   *
   * @return {@code true} if the reset password token has expired, otherwise {@code false}
   */
  public boolean isResetPasswordTokenExpired() {
    return isNull(resetPasswordTokenExpiryDate) || resetPasswordTokenExpiryDate.isBefore(LocalDateTime.now());
  }

}
