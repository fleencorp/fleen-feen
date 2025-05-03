package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.calendar.CalendarNotFoundException;
import com.fleencorp.feen.exception.google.oauth2.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.CannotCancelOrDeleteOngoingStreamException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyCanceledException;
import com.fleencorp.feen.exception.stream.core.StreamAlreadyHappenedException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.model.dto.stream.base.*;
import com.fleencorp.feen.model.response.stream.base.*;
import com.fleencorp.feen.model.security.FleenUser;

public interface CommonStreamService {

  DeleteStreamResponse deleteStream(Long streamId, DeleteStreamDto deleteStreamDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException;

  CancelStreamResponse cancelStream(Long streamId, CancelStreamDto cancelStreamDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, CannotCancelOrDeleteOngoingStreamException,
      FailedOperationException;

  RescheduleStreamResponse rescheduleStream(Long streamId, RescheduleStreamDto rescheduleStreamDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, StreamNotCreatedByUserException,
    StreamAlreadyHappenedException, StreamAlreadyCanceledException, FailedOperationException;

  UpdateStreamResponse updateStream(Long streamId, UpdateStreamDto updateStreamDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      FailedOperationException;

  UpdateStreamResponse updateStreamOtherDetails(Long streamId, UpdateStreamOtherDetailDto updateStreamOtherDetailDto, FleenUser user)
    throws StreamNotFoundException, StreamNotCreatedByUserException, StreamAlreadyHappenedException,
      StreamAlreadyCanceledException, FailedOperationException;

  UpdateStreamVisibilityResponse updateStreamVisibility(Long eventId, UpdateStreamVisibilityDto updateStreamVisibilityDto, FleenUser user)
    throws StreamNotFoundException, CalendarNotFoundException, Oauth2InvalidAuthorizationException,
      StreamNotCreatedByUserException, StreamAlreadyHappenedException, StreamAlreadyCanceledException,
      CannotCancelOrDeleteOngoingStreamException, FailedOperationException;
}
