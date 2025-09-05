package com.fleencorp.feen.user.controller.profile;

import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.user.model.dto.profile.*;
import com.fleencorp.feen.user.model.response.RemoveProfilePhotoResponse;
import com.fleencorp.feen.user.model.response.SendUpdateEmailOrPhoneVerificationCodeResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveMemberUpdateInfoResponse;
import com.fleencorp.feen.user.model.response.read.RetrieveProfileStatusResponse;
import com.fleencorp.feen.user.model.response.update.*;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.feen.user.service.member.MemberUpdateService;
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
  public RetrieveMemberInfoResponse retrieveInfo(@AuthenticationPrincipal final RegisteredUser user) {
    return memberService.getMemberInfo(user);
  }

  @GetMapping(value = "/info-update")
  public RetrieveMemberUpdateInfoResponse retrieveUpdateInfo(@AuthenticationPrincipal final RegisteredUser user) {
    return memberService.getMemberUpdateInfo(user);
  }

  @GetMapping(value = "/status")
  public RetrieveProfileStatusResponse retrieveStatus(@AuthenticationPrincipal final RegisteredUser user) {
    return memberService.getProfileStatus(user);
  }

  @PutMapping(value = "/update-password")
  public UpdatePasswordResponse updatePassword(
      @Valid @RequestBody final UpdatePasswordDto updatePasswordDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updatePassword(updatePasswordDto, user);
  }

  @PutMapping(value = "/update-info")
  public UpdateProfileInfoResponse updateInfo(
      @Valid @RequestBody final UpdateProfileInfoDto updateProfileInfoDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updateInfo(updateProfileInfoDto, user);
  }

  @PostMapping(value = "/update-email-phone")
  public SendUpdateEmailOrPhoneVerificationCodeResponse startUpdateEmailPhone(
      @Valid @RequestBody final UpdateEmailAddressOrPhoneNumberDto updateEmailAddressOrPhoneNumberDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.sendUpdateEmailAddressOrPhoneNumberVerificationCode(updateEmailAddressOrPhoneNumberDto, user);
  }

  @PutMapping(value = "/update-email")
  public UpdateEmailAddressResponse updateEmail(
      @Valid @RequestBody final ConfirmUpdateEmailAddressDto confirmUpdateEmailAddressDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updateEmailAddress(confirmUpdateEmailAddressDto, user);
  }

  @PutMapping(value = "/update-phone")
  public UpdatePhoneNumberResponse updatePhone(
      @Valid @RequestBody final ConfirmUpdatePhoneNumberDto confirmUpdatePhoneNumberDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updatePhoneNumber(confirmUpdatePhoneNumberDto, user);
  }

  @PutMapping(value = "/update-photo")
  public UpdateProfilePhotoResponse updatePhoto(
      @Valid @RequestBody final UpdateProfilePhotoDto updateProfilePhotoDto,
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updateProfilePhoto(updateProfilePhotoDto, user);
  }

  @PutMapping(value = "/update-status-active")
  public UpdateProfileStatusResponse updateStatusActive(
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updateProfileActive(user);
  }

  @PutMapping(value = "/update-status-inactive")
  public UpdateProfileStatusResponse updateStatusInactive(
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.updateProfileInactive(user);
  }

  @DeleteMapping(value = "/remove-photo")
  public RemoveProfilePhotoResponse removePhoto(
      @AuthenticationPrincipal final RegisteredUser user) {
    return memberUpdateService.removeProfilePhoto(user);
  }
}
