package com.fleencorp.feen.shared.stream.service;

import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.shared.stream.contract.IsAttendee;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface StreamQueryService {

  Optional<IsAStream> findStreamById(Long streamId);

  Optional<IsAStream> findStreamChatSpaceById(Long streamId);

  Optional<IsAttendee> findAttendeeById(Long attendeeId);

  @SuppressWarnings("unchecked")
  Page<IsAStream> findCommonPastAttendedStreams(
    Long memberAId,
    Long memberBId,
    Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable
  );

  @SuppressWarnings("unchecked")
  Page<IsAStream> findStreamsCreatedByMember(
    Long memberId,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable
  );
}
