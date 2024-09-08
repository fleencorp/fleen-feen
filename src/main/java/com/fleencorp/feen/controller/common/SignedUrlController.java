package com.fleencorp.feen.controller.common;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.constant.http.StatusCodeMessage;
import com.fleencorp.feen.model.response.external.aws.SignedUrlResponse;
import com.fleencorp.feen.service.common.ObjectService;
import com.fleencorp.feen.service.impl.external.aws.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/signed-url")
@PreAuthorize("isFullyAuthenticated()")
public class SignedUrlController {

  private final ObjectService objectService;
  private final S3Service s3Service;
  private final S3BucketNames s3BucketNames;

  public SignedUrlController(
      final ObjectService objectService,
      final S3Service s3Service,
      final S3BucketNames s3BucketNames) {
    this.objectService = objectService;
    this.s3Service = s3Service;
    this.s3BucketNames = s3BucketNames;
  }

  @Operation(summary = "Generate a signed url for a cover photo or thumbnail upload",
    description = "Generate an S3 Signed URL to upload cover photo for a stream.")
  @ApiResponse(responseCode = "200", description = "Signed URL generated successfully",
    content = { @Content(schema = @Schema(implementation = SignedUrlResponse.class))
  })
  @ApiResponse(responseCode = "500", description = StatusCodeMessage.RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping("/stream-thumbnail")
  public SignedUrlResponse forStreamCoverPhotoOrThumbnail(
    @Parameter(description = "The file or object name", required = true)
      @RequestParam(name = "file_name") final String fileName) {
    final String generatedFileName = objectService.generateRandomNameForFile(fileName);
    final String fileContentType = s3Service.detectContentType(generatedFileName);
    final String bucketName = s3BucketNames.getStreamCoverPhoto();

    final String signedUrl = s3Service.generateSignedUrl(bucketName, generatedFileName, fileContentType);
    return new SignedUrlResponse(signedUrl);
  }

  @Operation(summary = "Generate a signed url for photo upload",
    description = "Generate an S3 Signed URL to upload user profile display photo to S3 Bucket.")
  @ApiResponse(responseCode = "200", description = "Signed URL generated successfully",
    content = { @Content(schema = @Schema(implementation = SignedUrlResponse.class))
  })
  @ApiResponse(responseCode = "500", description = StatusCodeMessage.RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @GetMapping("/profile-photo")
  public SignedUrlResponse forProfilePhoto(
    @Parameter(description = "The file or object name", required = true)
      @RequestParam(name = "file_name") final String fileName) {
    final String generatedFileName = objectService.generateRandomNameForFile(fileName);
    final String fileContentType = s3Service.detectContentType(generatedFileName);
    final String bucketName = s3BucketNames.getUserPhoto();

    final String signedUrl = s3Service.generateSignedUrl(bucketName, generatedFileName, fileContentType);
    return new SignedUrlResponse(signedUrl);
  }
}
