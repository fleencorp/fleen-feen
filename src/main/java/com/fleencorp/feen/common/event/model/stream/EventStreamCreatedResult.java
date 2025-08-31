package com.fleencorp.feen.common.event.model.stream;

import com.fleencorp.feen.common.constant.base.ResultType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStreamCreatedResult extends ResultData {

  private String streamId;
  private String eventId;
  private String streamLink;

  private String message = "Event stream created successfully";

  public static EventStreamCreatedResult of(final Object userId, final Object streamId, final String eventId, final String streamLink, final ResultType resultType) {
    final EventStreamCreatedResult result = new EventStreamCreatedResult();
    result.setUserId(nonNull(userId) ? userId.toString() : null);
    result.setStreamId(nonNull(streamId) ? streamId.toString() : null);
    result.setEventId(eventId);
    result.setStreamLink(streamLink);
    result.setResultType(resultType);

    return result;
  }
}
