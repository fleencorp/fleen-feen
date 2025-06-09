package com.fleencorp.feen.service.security.mfa;

import com.fleencorp.feen.user.constant.mfa.MfaType;
import com.fleencorp.feen.user.model.dto.security.mfa.ConfirmSetupMfaDto;
import com.fleencorp.feen.user.model.dto.security.mfa.SetupMfaDto;
import com.fleencorp.feen.model.response.security.mfa.ConfirmMfaSetupResponse;
import com.fleencorp.feen.model.response.security.mfa.EnableOrDisableMfaResponse;
import com.fleencorp.feen.model.response.security.mfa.MfaStatusResponse;
import com.fleencorp.feen.model.response.security.mfa.SetupMfaResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

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
