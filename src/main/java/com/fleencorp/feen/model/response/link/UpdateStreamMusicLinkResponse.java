package com.fleencorp.feen.model.response.link;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
  "message"
})
public class UpdateStreamMusicLinkResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "update.stream.music.link";
  }

  public static UpdateStreamMusicLinkResponse of() {
    return new UpdateStreamMusicLinkResponse();
  }
}
