package com.fleencorp.feen.stream.service.speaker;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.speaker.OrganizerOfStreamCannotBeRemovedAsSpeakerException;
import com.fleencorp.feen.stream.model.dto.core.RemoveStreamSpeakerDto;
import com.fleencorp.feen.stream.model.dto.speaker.MarkAsStreamSpeakerDto;
import com.fleencorp.feen.stream.model.dto.speaker.UpdateStreamSpeakerDto;
import com.fleencorp.feen.stream.model.request.search.StreamSpeakerSearchRequest;
import com.fleencorp.feen.stream.model.response.speaker.MarkAsStreamSpeakerResponse;
import com.fleencorp.feen.stream.model.response.speaker.RemoveStreamSpeakerResponse;
import com.fleencorp.feen.stream.model.response.speaker.UpdateStreamSpeakerResponse;
import com.fleencorp.feen.stream.model.search.speaker.StreamSpeakerSearchResult;
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
