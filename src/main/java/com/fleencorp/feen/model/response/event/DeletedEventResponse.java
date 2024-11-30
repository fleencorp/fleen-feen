package com.fleencorp.feen.model.response.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "event_id",
  "is_deleted_info"
})
public class DeletedEventResponse extends ApiResponse {

  @JsonProperty("event_id")
  private Long eventId;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo isDeletedInfo;

  @Override
  public String getMessageCode() {
    return "deleted.event";
  }

  public static DeletedEventResponse of(final long eventId, final IsDeletedInfo isDeletedInfo) {
    return DeletedEventResponse.builder()
            .eventId(eventId)
            .isDeletedInfo(isDeletedInfo)
            .build();
  }
}
