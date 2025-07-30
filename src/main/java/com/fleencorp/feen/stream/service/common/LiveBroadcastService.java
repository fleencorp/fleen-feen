package com.fleencorp.feen.stream.service.common;

import com.fleencorp.feen.stream.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.stream.model.response.base.CreateStreamResponse;
import com.fleencorp.feen.stream.model.response.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface LiveBroadcastService {

  DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast();

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, RegisteredUser user) throws Oauth2InvalidAuthorizationException;
}
