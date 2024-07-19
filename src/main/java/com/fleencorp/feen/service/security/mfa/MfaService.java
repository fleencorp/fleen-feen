package com.fleencorp.feen.service.security.mfa;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.model.dto.security.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.model.dto.security.mfa.SetupMfaDto;
import com.fleencorp.feen.model.response.security.mfa.EnableOrDisableMfaResponse;
import com.fleencorp.feen.model.response.security.mfa.MfaStatusResponse;
import com.fleencorp.feen.model.response.security.mfa.SetupMfaResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface MfaService {

  EnableOrDisableMfaResponse reEnableMfa(FleenUser user);

  EnableOrDisableMfaResponse disableMfa(FleenUser user);

  MfaStatusResponse getMfaStatus(FleenUser user);

  SetupMfaResponse setupMfa(FleenUser user, SetupMfaDto mfaTypeDto);

  SetupMfaResponse resendMfaSetupCode(FleenUser user, SetupMfaDto mfaTypeDto);

  void confirmMfaSetup(FleenUser fleenUser, ConfirmSetupMfaDto dto);

  boolean isPhoneOrEmailMfaType(MfaType mfaType);

  void validateEmailOrPhoneMfaVerificationCode(String otpCode, String username);

  boolean isAuthenticatorMfaType(MfaType mfaType);

  void validateAuthenticatorMfaVerificationCode(String otpCode, Long userId);

}
