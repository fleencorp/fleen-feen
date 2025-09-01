package com.fleencorp.feen.stream.service.core;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.model.response.StreamResponse;

import java.util.Collection;

public interface CommonStreamOtherService {

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, IsAMember member);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);
}
