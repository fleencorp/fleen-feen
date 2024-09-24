package com.fleencorp.feen.model.response.stream.speaker;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.security.mask.MaskedEmailAddress;
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
  "speaker_id",
  "member_id",
  "full_name",
  "email_address",
  "title",
  "description",
})
public class StreamSpeakerResponse {

  @JsonProperty("speaker_id")
  private Long speakerId;

  @JsonProperty("member_id")
  private Long memberId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("title")
  private String title;

  @JsonProperty("description")
  private String description;

  @JsonProperty("email_address")
  private MaskedEmailAddress emailAddress;
}
