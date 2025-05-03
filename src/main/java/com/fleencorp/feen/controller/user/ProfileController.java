package com.fleencorp.feen.controller.user;

import com.fleencorp.feen.model.dto.user.profile.*;
import com.fleencorp.feen.model.response.user.profile.RemoveProfilePhotoResponse;
import com.fleencorp.feen.model.response.user.profile.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.model.response.user.profile.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.model.response.user.profile.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.model.response.user.profile.read.RetrieveProfileStatusResponse;
import com.fleencorp.feen.model.response.user.profile.update.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.feen.service.user.MemberUpdateService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/profile")
@PreAuthorize("isFullyAuthenticated()")
public class ProfileController {

  private final MemberService memberService;
  private final MemberUpdateService memberUpdateService;

  public ProfileController(
      final MemberService memberService,
      final MemberUpdateService memberUpdateService) {
    this.memberService = memberService;
    this.memberUpdateService = memberUpdateService;
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
    return memberUpdateService.updatePassword(updatePasswordDto, user);
  }

  @PutMapping(value = "/update-info")
  public UpdateProfileInfoResponse updateInfo(
      @Valid @RequestBody final UpdateProfileInfoDto updateProfileInfoDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updateInfo(updateProfileInfoDto, user);
  }

  @PostMapping(value = "/update-email-phone")
  public SendUpdateEmailOrPhoneVerificationCodeResponse startUpdateEmailPhone(
      @Valid @RequestBody final UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.sendUpdateEmailAddressOrPhoneNumberVerificationCode(updateEmailAddressOrPhoneNumberDto, user);
  }

  @PutMapping(value = "/update-email")
  public UpdateEmailAddressResponse updateEmail(
      @Valid @RequestBody final ConfirmUpdateEmailAddressDto confirmUpdateEmailAddressDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updateEmailAddress(confirmUpdateEmailAddressDto, user);
  }

  @PutMapping(value = "/update-phone")
  public UpdatePhoneNumberResponse updatePhone(
      @Valid @RequestBody final ConfirmUpdatePhoneNumberDto confirmUpdatePhoneNumberDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updatePhoneNumber(confirmUpdatePhoneNumberDto, user);
  }

  @PutMapping(value = "/update-photo")
  public UpdateProfilePhotoResponse updatePhoto(
      @Valid @RequestBody final UpdateProfilePhotoDto updateProfilePhotoDto,
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updateProfilePhoto(updateProfilePhotoDto, user);
  }

  @PutMapping(value = "/update-status-active")
  public UpdateProfileStatusResponse updateStatusActive(
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updateProfileActive(user);
  }

  @PutMapping(value = "/update-status-inactive")
  public UpdateProfileStatusResponse updateStatusInactive(
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.updateProfileInactive(user);
  }

  @DeleteMapping(value = "/remove-photo")
  public RemoveProfilePhotoResponse removePhoto(
      @AuthenticationPrincipal final FleenUser user) {
    return memberUpdateService.removeProfilePhoto(user);
  }
}
