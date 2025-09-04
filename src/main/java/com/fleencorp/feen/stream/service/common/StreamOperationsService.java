package com.fleencorp.feen.stream.service.common;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface StreamOperationsService {

  Page<FleenStream> findByChatSpaceId(Long chatSpaceId, Pageable pageable);

  void updateTotalAttendeeCount(Long streamId, boolean increment);

  Long countTotalStreamsAttended(IsAMember member);

  Long countTotalStreamsAttended(StreamType streamType, IsAMember member);

  Long countTotalStreamsByUser(IsAMember member);

  Long countTotalStreamsByUser(StreamType streamType, IsAMember member);

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, Long userId);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, Long userId);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, IsAMember user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;

  boolean existsByAttendees(IsAMember viewer, IsAMember target);

  Integer updateLikeCount(Long streamId, boolean isLiked);

  Integer updateBookmarkCount(Long streamId, boolean increment);

  FleenStream save(FleenStream stream);

  void updateExternalId(Long streamId, String externalId);

  void updateExternalIdAndLink(Long streamId, String externalId, String link);

  Optional<FleenStream> findById(Long streamId);

  boolean existsByAttendees(Long memberId, Long targetMemberId);

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, IsAMember member);

  void setStreamAttendeesAndTotalAttendeesAttending(StreamResponse streamResponse);

  void setFirst10AttendeesAttendingInAnyOrderOnStreams(StreamResponse streamResponse);

  Page<FleenStream> findStreamsCreatedByMember(Long memberId, Collection<StreamStatus> includedStatuses, Pageable pageable);

  Page<FleenStream> findCommonPastAttendedStreams(
    Long memberAId,
    Long memberBId,
    Collection<StreamAttendeeRequestToJoinStatus> approvedStatuses,
    Collection<StreamStatus> includedStatuses,
    Pageable pageable);
}
