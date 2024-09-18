package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "member_id",
  "full_name",
  "title",
  "description"
})
public class StreamSpeakerResponse {

  @JsonProperty("member_id")
  private Long memberId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;
}
