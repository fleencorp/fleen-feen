package com.fleencorp.feen.link.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class LinkStreamMusicUpdateResponse extends LocalizedResponse {

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "update.stream.music.link";
  }

  public static LinkStreamMusicUpdateResponse of() {
    return new LinkStreamMusicUpdateResponse();
  }
}
