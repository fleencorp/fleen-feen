package com.fleencorp.feen.service.stream.common;

import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.response.stream.StreamResponse;

import java.util.Collection;

public interface CommonStreamOtherService {

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, Member member);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);
}
