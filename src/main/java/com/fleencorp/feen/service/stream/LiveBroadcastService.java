package com.fleencorp.feen.service.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.response.broadcast.CreateStreamResponse;
import com.fleencorp.feen.model.response.broadcast.RescheduleStreamResponse;
import com.fleencorp.feen.model.response.broadcast.UpdateStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastService {

  SearchResultView findLiveBroadcasts(LiveBroadcastSearchRequest searchRequest);

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, FleenUser user);

  UpdateStreamResponse updateLiveBroadcast(Long streamId, UpdateLiveBroadcastDto updateLiveBroadcastDto, FleenUser user);

  RescheduleStreamResponse rescheduleLiveBroadcast(Long streamId, RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, FleenUser user);
}
