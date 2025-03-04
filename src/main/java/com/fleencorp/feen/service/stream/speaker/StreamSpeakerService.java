package com.fleencorp.feen.service.stream.speaker;

import com.fleencorp.feen.model.dto.stream.base.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.DeleteStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.GetStreamSpeakersResponse;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamSpeakerService {

  StreamSpeakerSearchResult findSpeakers(Long streamId, StreamSpeakerSearchRequest searchRequest);

  GetStreamSpeakersResponse findStreamSpeakers(Long streamId);

  MarkAsStreamSpeakerResponse markAsSpeaker(Long streamId, MarkAsStreamSpeakerDto dto, FleenUser user);

  UpdateStreamSpeakerResponse updateSpeakers(Long streamId, UpdateStreamSpeakerDto dto, FleenUser user);

  DeleteStreamSpeakerResponse deleteSpeakers(Long streamId, DeleteStreamSpeakerDto dto, FleenUser user);
}
