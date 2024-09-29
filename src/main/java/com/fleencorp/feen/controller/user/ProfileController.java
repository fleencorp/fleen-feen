package com.fleencorp.feen.controller.user;

import com.fleencorp.feen.model.dto.user.profile.*;
import com.fleencorp.feen.model.response.user.profile.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.user.MemberService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/profile")
@PreAuthorize("isFullyAuthenticated()")
public class ProfileController {

  private final MemberService memberService;

  public ProfileController(final MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping(value = "/info")
  public RetrieveMemberInfoResponse retrieveInfo(@AuthenticationPrincipal final FleenUser user) {
    return memberService.getMemberInfo(user);
  }

  @GetMapping(value = "/info-update")
  public RetrieveMemberUpdateInfoResponse retrieveUpdateInfo(@AuthenticationPrincipal final FleenUser user) {
    return memberService.getMemberUpdateInfo(user);
  }

  @GetMapping(value = "/status")
  public RetrieveProfileStatusResponse retrieveStatus(@AuthenticationPrincipal final FleenUser user) {
    return memberService.getProfileStatus(user);
  }

  @PutMapping(value = "/update-password")
  public UpdatePasswordResponse updatePassword(
      @Valid @RequestBody final UpdatePasswordDto updatePasswordDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updatePassword(updatePasswordDto, user);
  }

  @PutMapping(value = "/update-info")
  public UpdateProfileInfoResponse updateInfo(
      @Valid @RequestBody final UpdateProfileInfoDto updateProfileInfoDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updateInfo(updateProfileInfoDto, user);
  }

  @PostMapping(value = "/update-email-phone")
  public SendUpdateEmailOrPhoneVerificationCodeResponse startUpdateEmailPhone(
      @Valid @RequestBody final UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.sendUpdateEmailAddressOrPhoneNumberVerificationCode(updateEmailAddressOrPhoneNumberDto, user);
  }

  @PutMapping(value = "/update-email")
  public UpdateEmailAddressResponse updateEmail(
      @Valid @RequestBody final ConfirmUpdateEmailAddressDto confirmUpdateEmailAddressDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updateEmailAddress(confirmUpdateEmailAddressDto, user);
  }

  @PutMapping(value = "/update-phone")
  public UpdatePhoneNumberResponse updatePhone(
      @Valid @RequestBody final ConfirmUpdatePhoneNumberDto confirmUpdatePhoneNumberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updatePhoneNumber(confirmUpdatePhoneNumberDto, user);
  }

  @PutMapping(value = "/update-photo")
  public UpdateProfilePhotoResponse updatePhoto(
      @Valid @RequestBody final UpdateProfilePhotoDto updateProfilePhotoDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updateProfilePhoto(updateProfilePhotoDto, user);
  }

  @PutMapping(value = "/update-status-active")
  public UpdateProfileStatusResponse updateStatusActive(
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updateProfileActive(user);
  }

  @PutMapping(value = "/update-status-inactive")
  public UpdateProfileStatusResponse updateStatusInactive(
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.updateProfileInactive(user);
  }

  @DeleteMapping(value = "/remove-photo")
  public RemoveProfilePhotoResponse removePhoto(
      @AuthenticationPrincipal final FleenUser user) {
    return memberService.removeProfilePhoto(user);
  }
}
