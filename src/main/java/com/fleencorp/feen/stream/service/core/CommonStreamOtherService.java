package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Collection;

public interface CommonStreamOtherService {

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, Member member);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);
}
