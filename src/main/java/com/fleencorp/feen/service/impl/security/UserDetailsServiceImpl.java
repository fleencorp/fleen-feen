package com.fleencorp.feen.service.impl.security;

import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.repository.user.MemberRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.fleencorp.feen.model.security.FleenUser.fromMember;

/**
 * Implementation of the Spring Security UserDetailsService interface for loading user details by username.
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

  private final MemberRepository repository;

  /**
   * Constructs a new UserDetailsServiceImpl instance with the specified MemberRepository.
   *
   * @param repository The repository for accessing member data.
   */
  public UserDetailsServiceImpl(final MemberRepository repository) {
    this.repository = repository;
  }

  /**
   * Loads user details by the specified email address.
   *
   * @param emailAddress The email address of the user to load.
   * @return UserDetails object representing the user.
   * @throws UsernameNotFoundException If no user with the given email address is found.
   */
  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(final String emailAddress) throws UsernameNotFoundException {
    // Retrieve the member from the repository by email address
    final Member member = repository
      .findByEmailAddress(emailAddress)
      .orElseThrow(() -> new UsernameNotFoundException(emailAddress));

    // Convert the member to UserDetails
    return fromMember(member);
  }
}
