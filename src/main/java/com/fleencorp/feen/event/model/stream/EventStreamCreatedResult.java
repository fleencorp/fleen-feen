package com.fleencorp.feen.event.model.stream;

import com.fleencorp.feen.constant.base.ResultType;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStreamCreatedResult extends ResultData {

  private String userId;
  private String streamId;
  private String eventId;
  private String streamLink;

  @Builder.Default
  private String message = "Event stream created successfully";

  public static EventStreamCreatedResult of(Object userId, Object streamId, String eventId, String streamLink, ResultType resultType) {
    return EventStreamCreatedResult.builder()
        .userId(userId.toString())
        .streamId(streamId.toString())
        .eventId(eventId)
        .streamLink(streamLink)
        .resultType(resultType)
        .build();
  }
}
