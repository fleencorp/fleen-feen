package com.fleencorp.feen.service.stream;

import com.fleencorp.feen.model.dto.stream.AddStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.DeleteStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.response.stream.speaker.AddStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.DeleteStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.GetStreamSpeakersResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.security.FleenUser;

public interface StreamSpeakerService {

  Object findSpeakers(String nameOrFullNameOrUsernameOrEmailAddress);

  GetStreamSpeakersResponse getSpeakers(Long eventOrStreamId);

  AddStreamSpeakerResponse addSpeakers(Long eventOrStreamId, AddStreamSpeakerDto dto, FleenUser user);

  UpdateStreamSpeakerResponse updateSpeakers(Long eventOrStreamId, UpdateStreamSpeakerDto dto, FleenUser user);

  DeleteStreamSpeakerResponse deleteSpeakers(Long eventOrStreamId, DeleteStreamSpeakerDto dto, FleenUser user);
}
