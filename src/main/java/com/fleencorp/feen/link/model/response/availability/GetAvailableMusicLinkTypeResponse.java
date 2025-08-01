package com.fleencorp.feen.link.model.response.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.common.MusicLinkType;
import com.fleencorp.feen.link.model.info.MusicLinkTypeInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "message",
  "link_types"
})
public class GetAvailableMusicLinkTypeResponse extends LocalizedResponse {

  @JsonProperty("link_types")
  private Map<MusicLinkType, MusicLinkTypeInfo> linkTypes;

  @Override
  public String getMessageCode() {
    return "get.available.music.link.type";
  }

  public static GetAvailableMusicLinkTypeResponse of(final Map<MusicLinkType, MusicLinkTypeInfo> linkTypes) {
    return new GetAvailableMusicLinkTypeResponse(linkTypes);
  }
}
