package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.response.stream.base.CreateStreamResponse;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface LiveBroadcastService {

  DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast();

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, RegisteredUser user) throws Oauth2InvalidAuthorizationException;
}
