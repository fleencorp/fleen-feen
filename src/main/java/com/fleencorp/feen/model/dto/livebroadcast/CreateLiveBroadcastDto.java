package com.fleencorp.feen.model.dto.livebroadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.CreateStreamDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.stream.StreamSource.YOUTUBE_LIVE;
import static com.fleencorp.feen.constant.stream.StreamType.LIVE_STREAM;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveBroadcastDto extends CreateStreamDto {

  @NotNull(message = "{liveBroadcast.categoryId.NotBlank}")
  @IsNumber
  @JsonProperty("category_id")
  private String categoryId;

  @Size(min = 1, max = 1000, message = "{liveBroadcast.thumbnailUrl.Size}")
  @JsonProperty("thumbnail_link_or_url")
  private String thumbnailUrl;

  public FleenStream toFleenStream(final Member member) {
    final FleenStream fleenStream = toFleenStream();
    fleenStream.setMember(member);
    fleenStream.setStreamSource(YOUTUBE_LIVE);
    fleenStream.setStreamType(LIVE_STREAM);
    return fleenStream;
  }

  public Oauth2ServiceType getOauth2ServiceType() {
    return Oauth2ServiceType.youTube();
  }
}
