package com.fleencorp.feen.service.mfa;

import com.fleencorp.feen.constant.security.mfa.MfaType;
import com.fleencorp.feen.model.dto.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.model.dto.mfa.SetupMfaDto;
import com.fleencorp.feen.model.response.mfa.EnableOrDisableMfaResponse;
import com.fleencorp.feen.model.response.mfa.MfaStatusResponse;
import com.fleencorp.feen.model.response.mfa.SetupMfaResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface MfaService {

  EnableOrDisableMfaResponse reEnableMfa(FleenUser user);

  EnableOrDisableMfaResponse disableMfa(FleenUser user);

  MfaStatusResponse getMfaStatus(FleenUser user);

  SetupMfaResponse setupMfa(FleenUser user, SetupMfaDto mfaTypeDto);

  SetupMfaResponse resendMfaCode(FleenUser user, SetupMfaDto mfaTypeDto);

  void confirmMfaSetup(FleenUser fleenUser, ConfirmSetupMfaDto dto);

  boolean isPhoneOrEmailMfaType(MfaType mfaType);

  void validateEmailOrPhoneMfaVerificationCode(String otpCode, String username);

  boolean isAuthenticatorMfaType(MfaType mfaType);

  void validateAuthenticatorMfaVerificationCode(String otpCode, Long userId);

}
