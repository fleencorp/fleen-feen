package com.fleencorp.feen.service.user;

import com.fleencorp.feen.model.dto.user.profile.*;
import com.fleencorp.feen.model.response.user.profile.*;
import com.fleencorp.feen.model.security.FleenUser;

public interface MemberService {

  boolean isIdExists(Long memberId);

  RetrieveMemberInfoResponse getMemberInfo(FleenUser user);

  RetrieveMemberUpdateInfoResponse getMemberUpdateInfo(FleenUser user);

  RetrieveProfileStatusResponse getProfileStatus(FleenUser user);

  UpdatePasswordResponse updatePassword(UpdatePasswordDto updatePasswordDto, FleenUser user);

  UpdateProfileInfoResponse updateInfo(UpdateProfileInfoDto updateProfileInfoDto, FleenUser user);

  SendUpdateEmailOrPhoneVerificationCodeResponse sendUpdateEmailAddressOrPhoneNumberVerificationCode(UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto, FleenUser user);

  UpdateEmailAddressResponse updateEmailAddress(ConfirmUpdateEmailAddressDto updateEmailAddressDto, FleenUser user);

  UpdatePhoneNumberResponse updatePhoneNumber(ConfirmUpdatePhoneNumberDto updatePhoneNumberDto, FleenUser user);

  UpdateProfilePhotoResponse updateProfilePhoto(UpdateProfilePhotoDto updateProfilePhotoDto, FleenUser user);

  UpdateProfileStatusResponse updateProfileActive(FleenUser user);

  UpdateProfileStatusResponse updateProfileInactive(FleenUser user);

  RemoveProfilePhotoResponse removeProfilePhoto(FleenUser user);
}
