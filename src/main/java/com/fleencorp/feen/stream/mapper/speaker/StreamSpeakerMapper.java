package com.fleencorp.feen.stream.mapper.speaker;

import com.fleencorp.feen.stream.model.domain.StreamSpeaker;
import com.fleencorp.feen.stream.model.projection.StreamAttendeeInfoSelect;
import com.fleencorp.feen.stream.model.response.speaker.StreamSpeakerResponse;

import java.util.List;

public interface StreamSpeakerMapper {

  List<StreamSpeakerResponse> toStreamSpeakerResponses(List<StreamSpeaker> entries);

  List<StreamSpeakerResponse> toStreamSpeakerResponsesByProjection(List<StreamAttendeeInfoSelect> entries);
}
