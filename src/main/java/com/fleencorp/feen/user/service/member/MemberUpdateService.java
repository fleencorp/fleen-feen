package com.fleencorp.feen.user.service.member;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.exception.user.EmailAddressAlreadyExistsException;
import com.fleencorp.feen.user.exception.user.PhoneNumberAlreadyExistsException;
import com.fleencorp.feen.user.exception.user.UpdatePasswordFailedException;
import com.fleencorp.feen.user.exception.user.UpdateProfileInfoFailedException;
import com.fleencorp.feen.user.model.dto.profile.*;
import com.fleencorp.feen.user.model.response.RemoveProfilePhotoResponse;
import com.fleencorp.feen.user.model.response.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.user.model.response.update.*;
import com.fleencorp.feen.verification.exception.core.ExpiredVerificationCodeException;
import com.fleencorp.feen.verification.exception.core.InvalidVerificationCodeException;
import com.fleencorp.feen.verification.exception.core.VerificationFailedException;

public interface MemberUpdateService {

  UpdatePasswordResponse updatePassword(UpdatePasswordDto updatePasswordDto, RegisteredUser user) throws UpdatePasswordFailedException;

  UpdateProfileInfoResponse updateInfo(UpdateProfileInfoDto updateProfileInfoDto, RegisteredUser user) throws UpdateProfileInfoFailedException;

  SendUpdateEmailOrPhoneVerificationCodeResponse sendUpdateEmailAddressOrPhoneNumberVerificationCode(UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto, RegisteredUser user);

  UpdateEmailAddressResponse updateEmailAddress(ConfirmUpdateEmailAddressDto updateEmailAddressDto, RegisteredUser user)
    throws VerificationFailedException, ExpiredVerificationCodeException, InvalidVerificationCodeException,
      EmailAddressAlreadyExistsException;

  UpdatePhoneNumberResponse updatePhoneNumber(ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, RegisteredUser user)
    throws VerificationFailedException, ExpiredVerificationCodeException, InvalidVerificationCodeException,
      PhoneNumberAlreadyExistsException;

  UpdateProfilePhotoResponse updateProfilePhoto(UpdateProfilePhotoDto updateProfilePhotoDto, RegisteredUser user) throws FailedOperationException;

  UpdateProfileStatusResponse updateProfileActive(RegisteredUser user) throws FailedOperationException;

  UpdateProfileStatusResponse updateProfileInactive(RegisteredUser user) throws FailedOperationException;

  RemoveProfilePhotoResponse removeProfilePhoto(RegisteredUser user) throws FailedOperationException;
}
