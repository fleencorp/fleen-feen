package com.fleencorp.feen.service.stream.speaker;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.exception.stream.speaker.OrganizerOfStreamCannotBeRemovedAsSpeakerException;
import com.fleencorp.feen.model.dto.stream.base.RemoveStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.model.dto.stream.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.model.request.search.stream.StreamSpeakerSearchRequest;
import com.fleencorp.feen.model.response.stream.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.model.response.stream.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.model.search.stream.speaker.StreamSpeakerSearchResult;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface StreamSpeakerService {

  StreamSpeakerSearchResult findSpeakers(Long streamId, StreamSpeakerSearchRequest searchRequest, RegisteredUser user);

  StreamSpeakerSearchResult findStreamSpeakers(Long streamId, StreamSpeakerSearchRequest searchRequest, RegisteredUser user);

  MarkAsStreamSpeakerResponse markAsSpeaker(Long streamId, MarkAsStreamSpeakerDto dto, RegisteredUser user)
    throws StreamNotFoundException, StreamNotCreatedByUserException, FailedOperationException;

  UpdateStreamSpeakerResponse updateSpeakers(Long streamId, UpdateStreamSpeakerDto dto, RegisteredUser user)
    throws StreamNotFoundException, OrganizerOfStreamCannotBeRemovedAsSpeakerException, FailedOperationException;

  RemoveStreamSpeakerResponse removeSpeakers(Long streamId, RemoveStreamSpeakerDto dto, RegisteredUser user)
    throws StreamNotFoundException, OrganizerOfStreamCannotBeRemovedAsSpeakerException, FailedOperationException;
}
