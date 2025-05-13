package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.security.FleenUser;

import java.util.Collection;

public interface CommonStreamOtherService {

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, FleenUser user);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);
}
