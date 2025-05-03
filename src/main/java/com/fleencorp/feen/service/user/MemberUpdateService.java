package com.fleencorp.feen.service.user;


import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.profile.EmailAddressAlreadyExistsException;
import com.fleencorp.feen.exception.user.profile.PhoneNumberAlreadyExistsException;
import com.fleencorp.feen.exception.user.profile.UpdatePasswordFailedException;
import com.fleencorp.feen.exception.user.profile.UpdateProfileInfoFailedException;
import com.fleencorp.feen.exception.verification.ExpiredVerificationCodeException;
import com.fleencorp.feen.exception.verification.InvalidVerificationCodeException;
import com.fleencorp.feen.exception.verification.VerificationFailedException;
import com.fleencorp.feen.model.dto.user.profile.*;
import com.fleencorp.feen.model.response.user.profile.RemoveProfilePhotoResponse;
import com.fleencorp.feen.model.response.user.profile.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.model.response.user.profile.update.*;
import com.fleencorp.feen.model.security.FleenUser;


public interface MemberUpdateService {

  UpdatePasswordResponse updatePassword(UpdatePasswordDto updatePasswordDto, FleenUser user) throws UpdatePasswordFailedException;

  UpdateProfileInfoResponse updateInfo(UpdateProfileInfoDto updateProfileInfoDto, FleenUser user) throws UpdateProfileInfoFailedException;

  SendUpdateEmailOrPhoneVerificationCodeResponse sendUpdateEmailAddressOrPhoneNumberVerificationCode(UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto, FleenUser user);

  UpdateEmailAddressResponse updateEmailAddress(ConfirmUpdateEmailAddressDto updateEmailAddressDto, FleenUser user)
    throws VerificationFailedException, ExpiredVerificationCodeException, InvalidVerificationCodeException,
      EmailAddressAlreadyExistsException;

  UpdatePhoneNumberResponse updatePhoneNumber(ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, FleenUser user)
    throws VerificationFailedException, ExpiredVerificationCodeException, InvalidVerificationCodeException,
      PhoneNumberAlreadyExistsException;

  UpdateProfilePhotoResponse updateProfilePhoto(UpdateProfilePhotoDto updateProfilePhotoDto, FleenUser user) throws FailedOperationException;

  UpdateProfileStatusResponse updateProfileActive(FleenUser user) throws FailedOperationException;

  UpdateProfileStatusResponse updateProfileInactive(FleenUser user) throws FailedOperationException;

  RemoveProfilePhotoResponse removeProfilePhoto(FleenUser user) throws FailedOperationException;
}
