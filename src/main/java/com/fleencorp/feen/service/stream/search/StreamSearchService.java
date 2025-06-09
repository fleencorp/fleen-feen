package com.fleencorp.feen.service.stream.search;

import com.fleencorp.feen.constant.stream.StreamTimeType;
import com.fleencorp.feen.model.request.search.stream.StreamSearchRequest;
import com.fleencorp.feen.model.request.search.stream.type.StreamTypeSearchRequest;
import com.fleencorp.feen.model.response.stream.base.RetrieveStreamResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.model.response.stream.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.model.search.stream.common.StreamSearchResult;
import com.fleencorp.feen.model.search.stream.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.model.search.stream.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface StreamSearchService {

  StreamSearchResult findStreams(StreamSearchRequest searchRequest, RegisteredUser user);

  StreamSearchResult findMyStreams(StreamSearchRequest searchRequest, RegisteredUser user);

  StreamSearchResult findStreamsPublic(StreamSearchRequest searchRequest, StreamTimeType streamTimeType);

  UserCreatedStreamsSearchResult findStreamsCreatedByUser(StreamSearchRequest searchRequest);

  StreamSearchResult findStreamsAttendedByUser(StreamSearchRequest searchRequest, RegisteredUser user);

  MutualStreamAttendanceSearchResult findStreamsAttendedWithAnotherUser(StreamSearchRequest searchRequest, RegisteredUser user);

  RetrieveStreamResponse retrieveStream(Long streamId, RegisteredUser user);

  TotalStreamsCreatedByUserResponse countTotalStreamsByUser(StreamTypeSearchRequest searchRequest, RegisteredUser user);

  TotalStreamsAttendedByUserResponse countTotalStreamsAttendedByUser(StreamTypeSearchRequest searchRequest, RegisteredUser user);
}
