package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.*;
import com.fleencorp.feen.model.dto.livebroadcast.CreateLiveBroadcastDto;
import com.fleencorp.feen.model.dto.stream.base.RescheduleStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamDto;
import com.fleencorp.feen.model.dto.stream.base.UpdateStreamVisibilityDto;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.response.stream.common.live.broadcast.DataForCreateLiveBroadcastResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface LiveBroadcastService {

  DataForCreateLiveBroadcastResponse getDataForCreateLiveBroadcast();

  CreateStreamResponse createLiveBroadcast(CreateLiveBroadcastDto createLiveBroadcastDto, FleenUser user) throws Oauth2InvalidAuthorizationException;

  UpdateStreamResponse updateLiveBroadcast(Long liveBroadcastId, UpdateStreamDto updateStreamDto, FleenUser user) throws Oauth2InvalidAuthorizationException;

  RescheduleStreamResponse rescheduleLiveBroadcast(Long liveBroadcastId, RescheduleStreamDto rescheduleStreamDto, FleenUser user)
    throws FleenStreamNotFoundException, Oauth2InvalidAuthorizationException;

  DeleteStreamResponse deleteLiveBroadcast(Long liveBroadcastId, FleenUser user)
    throws FleenStreamNotFoundException, Oauth2InvalidAuthorizationException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  CancelStreamResponse cancelLiveBroadcast(Long broadcastId, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  UpdateStreamVisibilityResponse updateLiveBroadcastVisibility(Long liveBroadcastId, UpdateStreamVisibilityDto updateStreamVisibilityDto, FleenUser user)
    throws FleenStreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException, FailedOperationException;
}
