package com.fleencorp.feen.service.impl.user;

import com.fleencorp.base.service.EmailService;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MemberServiceImpl implements MemberService, EmailService {

  private final MemberRepository memberRepository;

  public MemberServiceImpl(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @Override
  public boolean isEmailAddressExist(String emailAddress) {
    return memberRepository.existsByEmailAddress(emailAddress);
  }
}
