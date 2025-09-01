package com.fleencorp.feen.mfa.service;

import com.fleencorp.feen.mfa.constant.MfaType;
import com.fleencorp.feen.mfa.model.dto.ConfirmSetupMfaDto;
import com.fleencorp.feen.mfa.model.dto.SetupMfaDto;
import com.fleencorp.feen.mfa.model.response.ConfirmMfaSetupResponse;
import com.fleencorp.feen.mfa.model.response.EnableOrDisableMfaResponse;
import com.fleencorp.feen.mfa.model.response.MfaStatusResponse;
import com.fleencorp.feen.mfa.model.response.SetupMfaResponse;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface MfaService {

  EnableOrDisableMfaResponse reEnableMfa(RegisteredUser user);

  EnableOrDisableMfaResponse disableMfa(RegisteredUser user);

  MfaStatusResponse getMfaStatus(RegisteredUser user);

  SetupMfaResponse setupMfa(SetupMfaDto mfaTypeDto, RegisteredUser user);

  SetupMfaResponse resendMfaSetupCode(SetupMfaDto mfaTypeDto, RegisteredUser user);

  ConfirmMfaSetupResponse confirmMfaSetup(ConfirmSetupMfaDto dto, RegisteredUser user);

  boolean isPhoneOrEmailMfaType(MfaType mfaType);

  void validateEmailOrPhoneMfaVerificationCode(String otpCode, String username);

  boolean isAuthenticatorMfaType(MfaType mfaType);

  void validateAuthenticatorMfaVerificationCode(String otpCode, Long userId);

}
