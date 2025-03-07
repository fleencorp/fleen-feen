package com.fleencorp.feen.service.stream.speaker;

import com.fleencorp.feen.model.dto.stream.base.RemoveStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamSpeakerService {

  StreamSpeakerSearchResult findSpeakers(Long streamId, StreamSpeakerSearchRequest searchRequest, FleenUser user);

  StreamSpeakerSearchResult findStreamSpeakers(Long streamId, StreamSpeakerSearchRequest searchRequest, FleenUser user);

  MarkAsStreamSpeakerResponse markAsSpeaker(Long streamId, MarkAsStreamSpeakerDto dto, FleenUser user);

  UpdateStreamSpeakerResponse updateSpeakers(Long streamId, UpdateStreamSpeakerDto dto, FleenUser user);

  RemoveStreamSpeakerResponse removeSpeakers(Long streamId, RemoveStreamSpeakerDto dto, FleenUser user);
}
