package com.fleencorp.feen.common.controller;

import com.fleencorp.feen.shared.shared.count.model.dto.ShareDto;
import com.fleencorp.feen.shared.shared.count.model.response.ShareResponse;
import com.fleencorp.feen.shared.shared.count.service.ShareCountService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/share")
public class ShareCountController {

  private final ShareCountService shareCountService;

  public ShareCountController(final ShareCountService shareCountService) {
    this.shareCountService = shareCountService;
  }

  @PostMapping(value = "")
  public ShareResponse share(
      @Parameter(description = "Details for share", required = true)
      @Valid @RequestBody final ShareDto shareDto) {
    return shareCountService.share(shareDto);
  }
}
