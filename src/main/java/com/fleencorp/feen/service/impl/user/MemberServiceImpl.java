package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.repository.user.MemberRepository;
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

  /**
   * Constructs a new instance of {@code MemberServiceImpl} with the specified member repository.
   *
   * @param memberRepository the repository used to manage member entities.
   */
  public MemberServiceImpl(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
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
