package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Collection;

public interface CommonStreamOtherService {

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, Member member);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);
}
