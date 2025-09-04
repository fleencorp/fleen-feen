package com.fleencorp.feen.stream.model.dto.livebroadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.oauth2.constant.Oauth2ServiceType;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.core.CreateStreamDto;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.stream.constant.core.StreamSource.YOUTUBE_LIVE;
import static com.fleencorp.feen.stream.constant.core.StreamType.LIVE_STREAM;

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
    final FleenStream stream = toFleenStream();
    stream.setMemberId(member.getMemberId());
    stream.setStreamSource(YOUTUBE_LIVE);
    stream.setStreamType(LIVE_STREAM);
    return stream;
  }

  public Oauth2ServiceType getOauth2ServiceType() {
    return Oauth2ServiceType.youTube();
  }
}
