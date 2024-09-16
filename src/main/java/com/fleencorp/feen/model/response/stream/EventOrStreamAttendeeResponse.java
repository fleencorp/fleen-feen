package com.fleencorp.feen.model.response.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

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
  "organizer_comment"
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
}
