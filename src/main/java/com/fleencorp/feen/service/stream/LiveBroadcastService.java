package com.fleencorp.feen.service.stream;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.response.stream.DataForCreateStreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastService {

  DataForCreateStreamResponse getDataForCreateStream();

  SearchResultView findLiveBroadcasts(LiveBroadcastSearchRequest searchRequest);

  RetrieveStreamResponse retrieveStream(Long streamId);

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, FleenUser user);

  UpdateStreamResponse updateLiveBroadcast(Long streamId, UpdateLiveBroadcastDto updateLiveBroadcastDto, FleenUser user);

  RescheduleStreamResponse rescheduleLiveBroadcast(Long streamId, RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, FleenUser user);

  NotAttendingStreamResponse notAttendingStream(Long streamId, FleenUser user);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(Long streamId, ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, FleenUser user);

  DeletedStreamResponse deleteStream(Long streamId, FleenUser user);

  SearchResultView getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  UpdateStreamVisibilityResponse updateStreamVisibility(Long streamId, UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, FleenUser user);
}
