package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService, EmailService, PhoneService {

  private final MemberRepository memberRepository;

  public MemberServiceImpl(final MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public boolean isEmailAddressExist(final String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }

  @Override
  public boolean isPhoneNumberExist(final String phoneNumber) {
    return memberRepository.existsByPhoneNumber(phoneNumber);
  }
}
