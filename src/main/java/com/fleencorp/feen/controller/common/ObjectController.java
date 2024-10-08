package com.fleencorp.feen.controller.common;

import com.fleencorp.base.exception.FleenException;
import com.fleencorp.feen.configuration.external.aws.s3.S3BucketNames;
import com.fleencorp.feen.model.response.other.DeleteResponse;
import com.fleencorp.feen.service.i18n.LocalizedResponse;
import com.fleencorp.feen.service.impl.external.aws.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.fleencorp.feen.constant.http.StatusCodeMessage.RESPONSE_500;

@Slf4j
@RestController
@RequestMapping(value = "/api/object")
@PreAuthorize("hasAnyRole('USER', 'ADMINISTRATOR', 'SUPER_ADMINISTRATOR')")
public class ObjectController {

  private final S3Service s3Service;
  private final S3BucketNames s3BucketNames;
  private final LocalizedResponse localizedResponse;

  public ObjectController(
      final S3Service s3Service,
      final S3BucketNames s3BucketNames,
      final LocalizedResponse localizedResponse) {
    this.s3Service = s3Service;
    this.s3BucketNames = s3BucketNames;
    this.localizedResponse = localizedResponse;
  }

  @Operation(summary = "Delete Display or Profile Photo",
    description = "Delete the display or profile photo for a user.")
  @ApiResponse(responseCode = "200", description = "Display or Profile photo deleted successfully",
    content = { @Content(schema = @Schema(implementation = DeleteResponse.class))
  })
  @ApiResponse(responseCode = "500", description = RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @DeleteMapping(value = "/delete/profile-photo")
  public DeleteResponse deleteProfilePhoto(
    @Parameter(description = "Object key for the profile photo", required = true)
      @RequestParam(name = "key") final String key) {
    s3Service.deleteObjectSilent(s3BucketNames.getUserPhoto(), key);
    return localizedResponse.of(DeleteResponse.of());
  }

  @Operation(summary = "Delete a stream's cover photo or thumbnail",
    description = "Delete the cover photo or thumbnail for a event or stream.")
  @ApiResponse(responseCode = "200", description = "Stream cover photo or thumbnail deleted successfully",
    content = { @Content(schema = @Schema(implementation = DeleteResponse.class))
  })
  @ApiResponse(responseCode = "500", description = RESPONSE_500,
    content = { @Content(schema = @Schema(implementation = FleenException.class))
  })
  @DeleteMapping(value = "/delete/stream-cover-photo")
  public DeleteResponse deleteStreamCoverPhoto(
    @Parameter(description = "Object key for the stream cover photo or thumbnail", required = true)
      @RequestParam(name = "key") final String key) {
    s3Service.deleteObjectSilent(s3BucketNames.getStreamCoverPhoto(), key);
    return localizedResponse.of(DeleteResponse.of());
  }

}
