package com.fleencorp.feen.stream.service.common;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface StreamOperationsService {

  Page<FleenStream> findByChatSpaceId(Long chatSpaceId, Pageable pageable);

  void updateTotalAttendeeCount(Long streamId, boolean increment);

  Long countTotalStreamsAttended(Member member);

  Long countTotalStreamsAttended(StreamType streamType, Member member);

  Long countTotalStreamsByUser(Member member);

  Long countTotalStreamsByUser(StreamType streamType, Member member);

  FleenStream findStream(Long streamId) throws StreamNotFoundException;

  DataForRescheduleStreamResponse getDataForRescheduleStream();

  void increaseTotalAttendeesOrGuests(FleenStream stream);

  void validateStreamAndUserForProtectedStream(FleenStream stream, RegisteredUser user);

  void registerAndApproveOrganizerOfStreamAsAnAttendee(FleenStream stream, RegisteredUser user);

  StreamOtherDetailsHolder retrieveStreamOtherDetailsHolder(FleenStream stream, RegisteredUser user) throws CalendarNotFoundException, Oauth2InvalidAuthorizationException;

  boolean existsByAttendees(Member viewer, Member target);

  Integer updateLikeCount(Long streamId, boolean isLiked);

  Integer updateBookmarkCount(Long streamId, boolean increment);

  FleenStream save(FleenStream stream);

  Optional<FleenStream> findById(Long streamId);

  boolean existsByAttendees(Long memberId, Long targetMemberId);

  void processOtherStreamDetails(Collection<StreamResponse> streamResponses, Member member);

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
