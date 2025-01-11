package com.fleencorp.feen.service.stream.speaker;

import com.fleencorp.feen.model.dto.stream.base.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.AddStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.AddStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.DeleteStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.GetStreamSpeakersResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamSpeakerService {

  StreamSpeakerSearchResult findSpeakers(StreamSpeakerSearchRequest searchRequest);

  GetStreamSpeakersResponse getSpeakers(Long streamId);

  AddStreamSpeakerResponse addSpeakers(Long streamId, AddStreamSpeakerDto dto, FleenUser user);

  UpdateStreamSpeakerResponse updateSpeakers(Long streamId, UpdateStreamSpeakerDto dto, FleenUser user);

  DeleteStreamSpeakerResponse deleteSpeakers(Long streamId, DeleteStreamSpeakerDto dto, FleenUser user);
}
