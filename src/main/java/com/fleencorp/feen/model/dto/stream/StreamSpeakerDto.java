package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.ValidEmail;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.stream.StreamSpeaker;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.validator.MemberExist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamSpeakerDto {

  @IsNumber
  @JsonProperty("stream_speaker_id")
  private String streamSpeakerId;

  @IsNumber
  @MemberExist
  @JsonProperty("member_id")
  private String memberId;

  @NotBlank(message = "{speaker.fullName.NotBlank}")
  @Size(min = 10, max = 500, message = "{speaker.fullName.Size}")
  @ToTitleCase
  @JsonProperty("full_name")
  private String fullName;

  @NotBlank(message = "{speaker.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{speaker.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  private String title;

  @Size(max = 3000, message = "{speaker.description.Size}")
  @JsonProperty("description")
  private String description;

  @Size(max = 50, message = "{user.emailAddress.Size}")
  @ValidEmail
  @ToLowerCase
  private String emailAddress;

  private Long getActualStreamSpeakerId() {
    return nonNull(streamSpeakerId) ? Long.parseLong(streamSpeakerId) : null;
  }

  public StreamSpeaker toStreamSpeaker(final FleenStream stream) {
    final StreamSpeaker streamSpeaker = toStreamSpeaker();
    streamSpeaker.setFleenStream(stream);
    return streamSpeaker;
  }

  public StreamSpeaker toStreamSpeaker() {
    return StreamSpeaker.builder()
      .streamSpeakerId(getActualStreamSpeakerId())
      .fullName(fullName)
      .description(description)
      .title(title)
      .member(Member.of(memberId))
      .build();
  }
}
