package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "speaker_id",
  "attendee_id",
  "full_name",
  "username",
  "title",
  "description",
})
public class StreamSpeakerResponse {

  @JsonProperty("speaker_id")
  private Long speakerId;

  @JsonProperty("attendee_id")
  private Long attendeeId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;
}
