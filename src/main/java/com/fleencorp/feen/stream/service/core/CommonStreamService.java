package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.stream.exception.core.*;
import com.fleencorp.feen.stream.model.dto.core.*;
import com.fleencorp.feen.stream.model.response.base.*;
import com.fleencorp.feen.shared.security.RegisteredUser;

public interface CommonStreamService {

  DeleteStreamResponse deleteStream(Long streamId, DeleteStreamDto deleteStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  CancelStreamResponse cancelStream(Long streamId, CancelStreamDto cancelStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
      FailedOperationException;

  RescheduleStreamResponse rescheduleStream(Long streamId, RescheduleStreamDto rescheduleStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  UpdateStreamResponse updateStream(Long streamId, UpdateStreamDto updateStreamDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      FailedOperationException;

  UpdateStreamResponse updateStreamOtherDetails(Long streamId, UpdateStreamOtherDetailDto updateStreamOtherDetailDto, RegisteredUser user)
    throws StreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
      StreamAlreadyCanceledException, FailedOperationException;

  UpdateStreamVisibilityResponse updateStreamVisibility(Long eventId, UpdateStreamVisibilityDto updateStreamVisibilityDto, RegisteredUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException;
}
