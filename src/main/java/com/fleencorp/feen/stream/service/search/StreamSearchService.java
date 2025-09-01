package com.fleencorp.feen.stream.service.search;

import com.fleencorp.feen.stream.constant.core.StreamTimeType;
import com.fleencorp.feen.stream.model.request.search.StreamSearchRequest;
import com.fleencorp.feen.stream.model.request.search.StreamTypeSearchRequest;
import com.fleencorp.feen.stream.model.response.base.RetrieveStreamResponse;
import com.fleencorp.feen.stream.model.response.statistic.TotalStreamsAttendedByUserResponse;
import com.fleencorp.feen.stream.model.response.statistic.TotalStreamsCreatedByUserResponse;
import com.fleencorp.feen.stream.model.search.common.StreamSearchResult;
import com.fleencorp.feen.stream.model.search.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.stream.model.search.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.shared.security.RegisteredUser;

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
