package com.fleencorp.feen.service.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.share.share.ExpectShareContactRequestDto;
import com.fleencorp.feen.model.dto.share.share.ProcessShareContactRequestDto;
import com.fleencorp.feen.model.dto.share.share.SendShareContactRequestDto;
import com.fleencorp.feen.model.request.search.share.ShareContactRequestSearchRequest;
import com.fleencorp.feen.model.response.share.share.CancelShareContactRequestResponse;
import com.fleencorp.feen.model.response.share.share.ExpectShareContactRequestResponse;
import com.fleencorp.feen.model.response.share.share.ProcessShareContactRequestResponse;
import com.fleencorp.feen.model.response.share.share.SendShareContactRequestResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface ShareContactRequestService {

  SearchResultView findShareContactRequests(ShareContactRequestSearchRequest searchRequest, FleenUser user);

  ExpectShareContactRequestResponse expectShareContactRequest(ExpectShareContactRequestDto expectShareContactRequestDto, FleenUser user);

  ProcessShareContactRequestResponse processShareContactRequest(Long shareContactRequestId, ProcessShareContactRequestDto processShareContactRequestDto, FleenUser user);

  SendShareContactRequestResponse sendShareContactRequest(SendShareContactRequestDto sendShareContactRequestDto, FleenUser user);

  CancelShareContactRequestResponse cancelShareContactRequest(Long shareContactRequestId, FleenUser user);
}
