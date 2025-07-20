package com.fleencorp.feen.common.controller;

import com.fleencorp.feen.common.model.response.EmailAddressExistsResponse;
import com.fleencorp.feen.common.model.response.PhoneNumberExistsResponse;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.common.service.misc.MiscService;
import com.fleencorp.feen.user.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/misc")
public class MiscController {

  private final MiscService miscService;
  private final MemberService memberService;

  public MiscController(
      final MemberService memberService,
      final MiscService miscService) {
    this.miscService = miscService;
    this.memberService = memberService;
  }

  @Operation(summary = "Check if an email address exists",
    description = "Checks if a member account with the given email address already exists in the system."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Check completed. Returns true if the email exists, false otherwise.",
      content = @Content(schema = @Schema(implementation = EmailAddressExistsResponse.class)))
  })
  @GetMapping(value = "/email-address/exists/{emailAddress}")
  public EmailAddressExistsResponse emailExists(
      @Parameter(description = "Email address to check for existence", required = true)
        @PathVariable(name = "emailAddress") final String emailAddress) {
    return memberService.verifyMemberEmailAddressExists(emailAddress);
  }

  @Operation(summary = "Check if a phone number exists",
    description = "Checks if a member account with the given phone number already exists in the system."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Check completed. Returns true if the phone number exists, false otherwise.",
      content = @Content(schema = @Schema(implementation = PhoneNumberExistsResponse.class)))
  })
  @GetMapping(value = "/phone-number/exists/{phoneNumber}")
  public PhoneNumberExistsResponse phoneExists(
      @Parameter(description = "Phone number to check for existence", required = true)
        @PathVariable(name = "phoneNumber") final String phoneNumber) {
    return memberService.verifyMemberPhoneNumberExists(phoneNumber);
  }

  @Operation(summary = "Encode a password eligible plain text",
    description = "Encodes and hashes a raw plain text password using the Bcrypt password encoder."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Raw plain text password encoded successfully",
      content = @Content(schema = @Schema(implementation = GetEncodedPasswordResponse.class)))
  })
  @GetMapping(value = "/get-encoded-password/{password}")
  public GetEncodedPasswordResponse getEncodedPasswordResponse(
      @Parameter(description = "Plain text password to be encoded", required = true)
        @PathVariable(name = "password") final String password) {
    return miscService.getEncodedPassword(password);
  }

}
