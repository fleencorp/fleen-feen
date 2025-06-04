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
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamSearchService {

  StreamSearchResult findStreams(StreamSearchRequest searchRequest, FleenUser user);

  StreamSearchResult findMyStreams(StreamSearchRequest searchRequest, FleenUser user);

  StreamSearchResult findStreamsPublic(StreamSearchRequest searchRequest, StreamTimeType streamTimeType);

  UserCreatedStreamsSearchResult findStreamsCreatedByUser(StreamSearchRequest searchRequest);

  StreamSearchResult findStreamsAttendedByUser(StreamSearchRequest searchRequest, FleenUser user);

  MutualStreamAttendanceSearchResult findStreamsAttendedWithAnotherUser(StreamSearchRequest searchRequest, FleenUser user);

  RetrieveStreamResponse retrieveStream(Long streamId, FleenUser user);

  TotalStreamsCreatedByUserResponse countTotalStreamsByUser(StreamTypeSearchRequest searchRequest, FleenUser user);

  TotalStreamsAttendedByUserResponse countTotalStreamsAttendedByUser(StreamTypeSearchRequest searchRequest, FleenUser user);
}
