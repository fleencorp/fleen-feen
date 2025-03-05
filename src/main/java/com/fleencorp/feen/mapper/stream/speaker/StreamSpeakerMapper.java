package com.fleencorp.feen.mapper.stream.speaker;

import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.projection.stream.attendee.StreamAttendeeInfoSelect;
import com.fleencorp.feen.model.response.stream.speaker.StreamSpeakerResponse;

import java.util.List;

public interface StreamSpeakerMapper {

  List<StreamSpeakerResponse> toStreamSpeakerResponses(List<StreamSpeaker> entries);

  List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(List<StreamAttendeeInfoSelect> entries);
}
