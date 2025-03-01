package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastService {

  DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast();

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, FleenUser user) throws Oauth2InvalidAuthorizationException;
}
