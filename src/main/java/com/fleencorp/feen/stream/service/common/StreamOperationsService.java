package com.fleencorp.feen.stream.service.common;

import com.fleencorp.feen.calendar.exception.core.CalendarNotFoundException;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.stream.constant.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.holder.StreamOtherDetailsHolder;
import com.fleencorp.feen.stream.model.response.StreamResponse;
import com.fleencorp.feen.stream.model.response.common.DataForRescheduleStreamResponse;
import com.fleencorp.feen.oauth2.exception.core.Oauth2InvalidAuthorizationException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface StreamOperationsService {

  Page<FleenStream> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate, StreamStatus status, Pageable pageable);

  Page<FleenStream> findByTitle(String title, StreamStatus status, Pageable pageable);

  Page<FleenStream> findMany(StreamStatus status, Pageable pageable);

  Page<FleenStream> findUpcomingStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findUpcomingStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findPastStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findPastStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findLiveStreams(LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findLiveStreamsByTitle(String title, LocalDateTime dateTime, StreamType streamType, Pageable pageable);

  Page<FleenStream> findByChatSpaceId(Long chatSpaceId, Pageable pageable);

  void incrementTotalAttendees(Long streamId);

  void decrementTotalAttendees(Long streamId);

  int incrementAndGetLikeCount(Long streamId);

  int decrementAndGetLikeCount(Long streamId);

  Page<FleenStream> findManyByMe(Member member, Pageable pageable);

  Page<FleenStream> findByTitleAndUser(String title, Member member, Pageable pageable);

  Page<FleenStream> findByTitleAndUser(String title, StreamVisibility streamVisibility, Member member, Pageable pageable);

  Page<FleenStream> findByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<FleenStream> findByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, StreamVisibility streamVisibility, Member member, Pageable pageable);

  Page<FleenStream> findAttendedByUser(Member member, Pageable pageable);

  Page<FleenStream> findAttendedByDateBetweenAndUser(LocalDateTime startDate, LocalDateTime endDate, Member member, Pageable pageable);

  Page<FleenStream> findAttendedByTitleAndUser(String title, Member member, Pageable pageable);

  Page<FleenStream> findStreamsAttendedTogether(Member you, Member friend, Pageable pageable);

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

  Long incrementLikeCount(Long streamId);

  Long decrementLikeCount(Long streamId);

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
