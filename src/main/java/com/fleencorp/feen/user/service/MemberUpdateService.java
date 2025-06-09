package com.fleencorp.feen.user.service;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.user.exception.user.profile.EmailAddressAlreadyExistsException;
import com.fleencorp.feen.user.exception.user.profile.PhoneNumberAlreadyExistsException;
import com.fleencorp.feen.user.exception.user.profile.UpdatePasswordFailedException;
import com.fleencorp.feen.user.exception.user.profile.UpdateProfileInfoFailedException;
import com.fleencorp.feen.user.exception.verification.ExpiredVerificationCodeException;
import com.fleencorp.feen.user.exception.verification.InvalidVerificationCodeException;
import com.fleencorp.feen.user.exception.verification.VerificationFailedException;
import com.fleencorp.feen.user.model.dto.profile.*;
import com.fleencorp.feen.user.model.response.RemoveProfilePhotoResponse;
import com.fleencorp.feen.user.model.response.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.user.model.response.update.*;
import com.fleencorp.feen.user.model.security.RegisteredUser;

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
