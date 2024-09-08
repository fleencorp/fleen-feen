package com.fleencorp.feen.controller.common;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.base.service.EmailService;
import com.fleencorp.base.service.PhoneService;
import com.fleencorp.feen.model.response.other.EntityExistsResponse;
import com.fleencorp.feen.model.response.security.GetEncodedPasswordResponse;
import com.fleencorp.feen.service.common.MiscService;
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

  private final EmailService emailService;
  private final PhoneService phoneService;
  private final MiscService miscService;

  public MiscController(
      final EmailService emailService,
      final PhoneService phoneService,
      final MiscService miscService) {
    this.emailService = emailService;
    this.phoneService = phoneService;
    this.miscService = miscService;
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
    final boolean exists = emailService.isEmailAddressExist(emailAddress);
    return new EntityExistsResponse(exists);
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
    final boolean exists = phoneService.isPhoneNumberExist(phoneNumber);
    return new EntityExistsResponse(exists);
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
