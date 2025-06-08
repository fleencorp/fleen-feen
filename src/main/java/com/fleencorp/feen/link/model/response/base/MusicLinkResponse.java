package com.fleencorp.feen.link.model.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.link.model.info.MusicLinkTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "url",
  "link_type",
  "is_updatable"
})
public class MusicLinkResponse {

  @JsonProperty("url")
  private String url;

  @JsonProperty("link_type")
  private MusicLinkTypeInfo linkType;

  public static MusicLinkResponse of(final String url, final MusicLinkTypeInfo linkTypeInfo) {
    return new MusicLinkResponse(url, linkTypeInfo);
  }
}
