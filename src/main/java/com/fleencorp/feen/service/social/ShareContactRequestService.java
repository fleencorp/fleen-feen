package com.fleencorp.feen.service.social;

import com.fleencorp.feen.model.dto.social.share.ExpectShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.ProcessShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.SendShareContactRequestDto;
import com.fleencorp.feen.model.request.search.social.ShareContactRequestSearchRequest;
import com.fleencorp.feen.model.response.social.share.CancelShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.ExpectShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.ProcessShareContactRequestResponse;
import com.fleencorp.feen.model.response.social.share.SendShareContactRequestResponse;
import com.fleencorp.feen.model.search.social.share.contact.ShareContactRequestSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface ShareContactRequestService {

  ShareContactRequestSearchResult findSentShareContactRequests(ShareContactRequestSearchRequest searchRequest, RegisteredUser user);

  ShareContactRequestSearchResult findReceivedShareContactRequests(ShareContactRequestSearchRequest searchRequest, RegisteredUser user);

  ShareContactRequestSearchResult findExpectedShareContactRequests(ShareContactRequestSearchRequest searchRequest, RegisteredUser user);

  ExpectShareContactRequestResponse expectShareContactRequest(ExpectShareContactRequestDto expectShareContactRequestDto, RegisteredUser user);

  SendShareContactRequestResponse sendShareContactRequest(SendShareContactRequestDto sendShareContactRequestDto, RegisteredUser user);

  ProcessShareContactRequestResponse processShareContactRequest(Long shareContactRequestId, ProcessShareContactRequestDto processShareContactRequestDto, RegisteredUser user);

  CancelShareContactRequestResponse cancelShareContactRequest(Long shareContactRequestId, RegisteredUser user);
}
