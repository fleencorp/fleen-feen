package com.fleencorp.feen.common.controller;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.feen.common.constant.http.StatusCodeMessage;
import com.fleencorp.feen.model.dto.aws.CreateSignedUrlDto;
import com.fleencorp.feen.model.response.external.aws.SignedUrlsResponse;
import com.fleencorp.feen.common.service.misc.ObjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/signed-url")
@PreAuthorize("isFullyAuthenticated()")
public class SignedUrlController {

  private final ObjectService objectService;

  public SignedUrlController(
      final ObjectService objectService) {
    this.objectService = objectService;
  }

  @Operation(summary = "Generate a signed url for a cover photo or thumbnail upload",
    description = "Generate an S3 Signed URL to upload cover photo for a stream.")
  @ApiResponse(responseCode = "200", description = "Signed URL generated successfully",
    content = { @Content(schema = @Schema(implementation = SignedUrlsResponse.class))
  })
  @ApiResponse(responseCode = "500", description = StatusCodeMessage.RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping("/stream-cover-photo-thumbnail")
  public SignedUrlsResponse forStreamCoverPhotoOrThumbnail(@Valid @RequestBody final CreateSignedUrlDto createSignedUrlDto) {
    createSignedUrlDto.streamCoverPhoto();
    return objectService.createSignedUrls(createSignedUrlDto);
  }

  @Operation(summary = "Generate a signed url for photo upload",
    description = "Generate an S3 Signed URL to upload user profile display photo to S3 Bucket.")
  @ApiResponse(responseCode = "200", description = "Signed URL generated successfully",
    content = { @Content(schema = @Schema(implementation = SignedUrlsResponse.class))
  })
  @ApiResponse(responseCode = "500", description = StatusCodeMessage.RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping("/profile-photo")
  public SignedUrlsResponse forProfilePhoto(@Valid @RequestBody final CreateSignedUrlDto createSignedUrlDto) {
    createSignedUrlDto.profilePhoto();
    return objectService.createSignedUrls(createSignedUrlDto);
  }
}
