package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.model.response.common.EmailAddressExistsResponse;
import com.fleencorp.feen.model.response.common.EmailAddressNotExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberExistsResponse;
import com.fleencorp.feen.model.response.common.PhoneNumberNotExistsResponse;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link MemberService}, {@link EmailService}, and {@link PhoneService} interfaces.
 *This class provides functionalities for managing members, including operations related to email and phone services.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService, EmailService, PhoneService {

  private final MemberRepository memberRepository;
  private final LocalizedResponse localizedResponse;

  /**
   * Constructs a new instance of {@code MemberServiceImpl} with the specified member repository.
   *
   * @param memberRepository the repository used to manage member entities.
   */
  public MemberServiceImpl(
      final MemberRepository memberRepository,
      final LocalizedResponse localizedResponse) {
    this.memberRepository = memberRepository;
    this.localizedResponse = localizedResponse;
  }

  /**
   * Checks if a member's email address exists in the system.
   *
   * <p>This method verifies the existence of the given email address
   * by calling {@link #isEmailAddressExist(String)}. Depending on whether
   * the email address exists or not, it returns a localized response
   * indicating the result.
   *
   * @param emailAddress the email address to check for existence
   * @return an {@link EntityExistsResponse} containing a localized response
   *         indicating whether the email address exists or not
   */
  @Override
  public EntityExistsResponse isMemberEmailAddressExists(String emailAddress) {
    boolean exists = isEmailAddressExist(emailAddress);
    return exists
      ? localizedResponse.of(EmailAddressExistsResponse.of(true))
      : localizedResponse.of(EmailAddressNotExistsResponse.of(false));
  }

  /**
   * Checks if a member with the specified email address exists in the repository.
   *
   * @param emailAddress the email address to check.
   * @return {@code true} if a member with the specified email address exists, {@code false} otherwise.
   */
  @Override
  public boolean isEmailAddressExist(final String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }

  /**
   * Checks if a member's phone number exists in the system.
   *
   * <p>This method verifies the existence of the given phone number
   * by calling {@link #isPhoneNumberExist(String)}. Based on the existence
   * of the phone number, it returns a localized response indicating
   * the result.
   *
   * @param phoneNumber the phone number to check for existence
   * @return an {@link EntityExistsResponse} containing a localized response
   *         indicating whether the phone number exists or not
   */
  @Override
  public EntityExistsResponse isMemberPhoneNumberExists(String phoneNumber) {
    boolean exists = isPhoneNumberExist(phoneNumber);
    return exists
      ? localizedResponse.of(PhoneNumberExistsResponse.of(true))
      : localizedResponse.of(PhoneNumberNotExistsResponse.of(false));
  }

  /**
   * Checks if a member with the specified phone number exists in the repository.
   *
   * @param phoneNumber the phone number to check.
   * @return {@code true} if a member with the specified phone number exists, {@code false} otherwise.
   */
  @Override
  public boolean isPhoneNumberExist(final String phoneNumber) {
    return memberRepository.existsByPhoneNumber(phoneNumber);
  }

  /**
   * Checks if a member with the specified ID exists in the repository.
   *
   * @param memberId the ID of the member to check.
   * @return {@code true} if the member exists, {@code false} otherwise.
   */
  @Override
  public boolean isIdExists(final Long memberId) {
    return memberRepository.existsByMemberId(memberId);
  }
}
