package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.stream.attendee.IsAttendingInfo;
import com.fleencorp.feen.model.info.stream.attendee.StreamAttendeeRequestToJoinStatusInfo;
import lombok.*;

import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "name",
  "display_photo",
  "comment",
  "organizer_comment",
  "request_to_join_status_info",
  "join_status_info",
  "is_attending_info"
})
public class EventOrStreamAttendeeResponse {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("name")
  private String name;

  @JsonProperty("display_photo")
  private String displayPhoto;

  @JsonProperty("comment")
  private String comment;

  @JsonProperty("organizer_comment")
  private String organizerComment;

  @JsonProperty("request_to_join_status_info")
  private StreamAttendeeRequestToJoinStatusInfo requestToJoinStatusInfo;

  @JsonProperty("join_status_info")
  private JoinStatusInfo joinStatusInfo;

  @JsonProperty("is_attending_info")
  private IsAttendingInfo isAttendingInfo;

  @JsonIgnore
  public boolean isAttending() {
    return nonNull(isAttendingInfo) && isAttendingInfo.getAttending();
  }
}
