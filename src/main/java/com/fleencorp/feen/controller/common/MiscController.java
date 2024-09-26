package com.fleencorp.feen.controller.common;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.service.common.MiscService;
import com.fleencorp.feen.service.user.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.fleencorp.feen.constant.http.StatusCodeMessage.RESPONSE_500;

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

  @Operation(summary = "Check If Email Address Exists",
    description = "Check if the specified email address exists.")
  @ApiResponse(responseCode = "200", description = "Email address existence checked successfully",
    content = { @Content(schema = @Schema(implementation = EntityExistsResponse.class))
  })
  @ApiResponse(responseCode = "500", description = RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping(value = "/email-address/exists/{emailAddress}")
  public EntityExistsResponse emailExists(
    @Parameter(description = "Email address to check for existence")
      @PathVariable(name = "emailAddress") final String emailAddress) {
    return memberService.isMemberEmailAddressExists(emailAddress);
  }

  @Operation(summary = "Check If Phone Number Exists",
    description = "Check if the specified phone number exists.")
  @ApiResponse(responseCode = "200", description = "Phone number existence checked successfully",
    content = { @Content(schema = @Schema(implementation = EntityExistsResponse.class))
  })
  @ApiResponse(responseCode = "500", description = RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping(value = "/phone-number/exists/{phoneNumber}")
  public EntityExistsResponse phoneExists(
    @Parameter(description = "Phone number to check for existence")
      @PathVariable(name = "phoneNumber") final String phoneNumber) {
    return memberService.isMemberPhoneNumberExists(phoneNumber);
  }

  @Operation(summary = "Encode a password eligible plain text",
    description = "Encode and hash a raw plain text with Bcrypt password encoder")
  @ApiResponse(responseCode = "200", description = "Raw plain text password encoded successfully",
    content = { @Content(schema = @Schema(implementation = GetEncodedPasswordResponse.class))
  })
  @ApiResponse(responseCode = "500", description = RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping(value = "/get-encoded-password/{password}")
  public GetEncodedPasswordResponse getEncodedPasswordResponse(
    @Parameter(description = "Create encoded password of a plain text password")
      @PathVariable(name = "password") final String password) {
    return miscService.getEncodedPassword(password);
  }

}
