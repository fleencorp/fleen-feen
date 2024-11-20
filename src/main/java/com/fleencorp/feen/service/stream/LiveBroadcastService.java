package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.RescheduleLiveBroadcastDto;
import com.fleencorp.feen.model.dto.livebroadcast.UpdateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.ProcessAttendeeRequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.RequestToJoinEventOrStreamDto;
import com.fleencorp.feen.model.dto.stream.UpdateEventOrStreamVisibilityDto;
import com.fleencorp.feen.model.request.search.stream.StreamAttendeeSearchRequest;
import com.fleencorp.feen.model.request.search.youtube.LiveBroadcastSearchRequest;
import com.fleencorp.feen.model.response.broadcast.*;
import com.fleencorp.feen.model.response.stream.EventOrStreamAttendeesResponse;
import com.fleencorp.feen.model.search.broadcast.LiveBroadcastSearchResult;
import com.fleencorp.feen.model.search.broadcast.request.RequestToJoinSearchResult;
import com.fleencorp.feen.model.search.stream.attendee.StreamAttendeeSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastService {

  DataForCreateStreamResponse getDataForCreateStream();

  LiveBroadcastSearchResult findLiveBroadcasts(LiveBroadcastSearchRequest searchRequest, FleenUser user);

  RetrieveStreamResponse retrieveStream(Long streamId, FleenUser user);

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, FleenUser user);

  UpdateStreamResponse updateLiveBroadcast(Long streamId, UpdateLiveBroadcastDto updateLiveBroadcastDto, FleenUser user);

  RescheduleStreamResponse rescheduleLiveBroadcast(Long streamId, RescheduleLiveBroadcastDto rescheduleLiveBroadcastDto, FleenUser user);

  NotAttendingStreamResponse notAttendingStream(Long streamId, FleenUser user);

  JoinStreamResponse joinStream(Long streamId, FleenUser user);

  RequestToJoinStreamResponse requestToJoinStream(Long streamId, RequestToJoinEventOrStreamDto requestToJoinEventOrStreamDto, FleenUser user);

  ProcessAttendeeRequestToJoinStreamResponse processAttendeeRequestToJoinStream(Long streamId, ProcessAttendeeRequestToJoinEventOrStreamDto processAttendeeRequestToJoinEventOrStreamDto, FleenUser user);

  DeletedStreamResponse deleteStream(Long streamId, FleenUser user);

  RequestToJoinSearchResult getAttendeeRequestsToJoinStream(Long streamId, StreamAttendeeSearchRequest searchRequest, FleenUser user);

  UpdateStreamVisibilityResponse updateStreamVisibility(Long streamId, UpdateEventOrStreamVisibilityDto updateEventOrStreamVisibilityDto, FleenUser user);

  StreamAttendeeSearchResult findStreamAttendees(Long streamId, StreamAttendeeSearchRequest searchRequest);

  EventOrStreamAttendeesResponse getStreamAttendees(Long streamId, StreamAttendeeSearchRequest streamAttendeeSearchRequest, FleenUser user);

  TotalStreamsCreatedByUserResponse countTotalStreamsByUser(FleenUser user);

  TotalStreamsAttendedByUserResponse countTotalStreamsAttended(FleenUser user);
}
