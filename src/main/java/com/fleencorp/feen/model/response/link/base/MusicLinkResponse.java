package com.fleencorp.feen.model.response.link.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.link.MusicLinkTypeInfo;
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

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;


  public static MusicLinkResponse of(String url, MusicLinkTypeInfo linkType) {
    return new MusicLinkResponse(url, linkType, false);
  }
}
