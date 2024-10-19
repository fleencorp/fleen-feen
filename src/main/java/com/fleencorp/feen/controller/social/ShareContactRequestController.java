package com.fleencorp.feen.controller.social;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.model.dto.social.share.ExpectShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.ProcessShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.SendShareContactRequestDto;
import com.fleencorp.feen.model.request.search.social.ShareContactRequestSearchRequest;
import com.fleencorp.feen.model.response.social.share.CancelShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.ExpectShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.ProcessShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.SendShareContactRequestResponse;
import com.fleencorp.feen.model.search.social.share.contact.ShareContactRequestSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.service.social.ShareContactRequestService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/share-contact-request")
public class ShareContactRequestController {

  private final ShareContactRequestService shareContactRequestService;

  public ShareContactRequestController(
      final ShareContactRequestService shareContactRequestService) {
    this.shareContactRequestService = shareContactRequestService;
  }

  @GetMapping(value = "/find-sent-requests")
  public ShareContactRequestSearchResult findSentRequests(
      @SearchParam final ShareContactRequestSearchRequest shareContactRequestSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.findSentShareContactRequests(shareContactRequestSearchRequest, user);
  }

  @GetMapping(value = "/find-received-requests")
  public ShareContactRequestSearchResult findReceivedRequests(
      @SearchParam final ShareContactRequestSearchRequest shareContactRequestSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.findReceivedShareContactRequests(shareContactRequestSearchRequest, user);
  }

  @GetMapping(value = "/find-sent-expected-requests")
  public ShareContactRequestSearchResult findSentExpectedRequests(
      @SearchParam final ShareContactRequestSearchRequest shareContactRequestSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    shareContactRequestSearchRequest.setSentExpectedRequest();
    return shareContactRequestService.findExpectedShareContactRequests(shareContactRequestSearchRequest, user);
  }

  @GetMapping(value = "/find-received-expected-requests")
  public ShareContactRequestSearchResult findReceivedExpectedRequests(
      @SearchParam final ShareContactRequestSearchRequest shareContactRequestSearchRequest,
      @AuthenticationPrincipal final FleenUser user) {
    shareContactRequestSearchRequest.setReceivedExpectedRequest();
    return shareContactRequestService.findExpectedShareContactRequests(shareContactRequestSearchRequest, user);
  }

  @PostMapping(value = "/expect-request")
  public ExpectShareContactRequestResponse expectRequest(
      @Valid @RequestBody final ExpectShareContactRequestDto expectShareContactRequestDto,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.expectShareContactRequest(expectShareContactRequestDto, user);
  }

  @PostMapping(value = "/send-request")
  public SendShareContactRequestResponse sendRequest(
      @Valid @RequestBody final SendShareContactRequestDto sendShareContactRequestDto,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.sendShareContactRequest(sendShareContactRequestDto, user);
  }

  @PutMapping(value = "/process-request/{shareContactRequestId}")
  public ProcessShareContactRequestResponse processRequest(
      @PathVariable(name = "shareContactRequestId") final Long shareContactRequestId,
      @Valid @RequestBody final ProcessShareContactRequestDto processShareContactRequestDto,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.processShareContactRequest(shareContactRequestId, processShareContactRequestDto, user);
  }

  @PutMapping(value = "/cancel-request/{shareContactRequestId}")
  public CancelShareContactRequestResponse cancelRequest(
      @PathVariable(name = "shareContactRequestId") final Long shareContactRequestId,
      @AuthenticationPrincipal final FleenUser user) {
    return shareContactRequestService.cancelShareContactRequest(shareContactRequestId, user);
  }

}
