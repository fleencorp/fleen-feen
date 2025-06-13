package com.fleencorp.feen.service.impl.auth;

import com.fleencorp.feen.event.publisher.ProfileRequestPublisher;
import com.fleencorp.feen.mapper.CommonMapper;
import com.fleencorp.feen.country.service.CountryService;
import com.fleencorp.feen.service.impl.cache.CacheService;
import com.fleencorp.feen.service.security.TokenService;
import com.fleencorp.feen.service.user.RoleService;
import com.fleencorp.feen.user.exception.auth.InvalidAuthenticationException;
import com.fleencorp.feen.user.model.dto.authentication.SignInDto;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.repository.MemberRepository;
import com.fleencorp.feen.user.service.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  MemberService memberService;

  @Mock
  CacheService cacheService;

  @Mock
  CountryService countryService;

  @Mock
  RoleService roleService;

  @Mock
  TokenService tokenService;

  @Mock
  MemberRepository memberRepository;

  @Mock
  PasswordEncoder passwordEncoder;

  @Mock
  ProfileRequestPublisher profileRequestPublisher;

  @Mock
  Localizer localizer;

  @Mock
  CommonMapper commonMapper;

  @InjectMocks
  AuthenticationServiceImpl authenticationService;

  @Test
  void ensure_credentials_invalid() {
    // given
    String emailAddress = "connor@example.com";
    String password = "#2#2C--r#2#2";

    SignInDto signInDto = new SignInDto();
    signInDto.setEmailAddress(emailAddress);
    signInDto.setPassword(password);

    // when
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenReturn(null);

    // then
    assertThrows(InvalidAuthenticationException.class, () -> authenticationService.signIn(signInDto));
  }

  @Test
  void ensure_credentials_valid() {
    // given
    String emailAddress = "connor@example.com";
    String password = "#2#2C--r#2#2";

    SignInDto signInDto = new SignInDto();
    signInDto.setEmailAddress(emailAddress);
    signInDto.setPassword(password);

    RegisteredUser user = new RegisteredUser();
    user.setEmailAddress(emailAddress);

    Authentication authenticatedUser = new UsernamePasswordAuthenticationToken(user, null, List.of());

    // when
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
    .thenReturn(authenticatedUser);

    // then
    assertDoesNotThrow(() -> authenticationService.signIn(signInDto));
  }
}