package com.fleencorp.feen.link.model.response.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.info.LinkTypeInfo;
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
public class GetAvailableLinkTypeResponse extends LocalizedResponse {

  @JsonProperty("link_types")
  private Map<LinkType, LinkTypeInfo> linkTypes;

  @Override
  public String getMessageCode() {
    return "get.available.link.type";
  }

  public static GetAvailableLinkTypeResponse of(final Map<LinkType, LinkTypeInfo> linkTypes) {
    return new GetAvailableLinkTypeResponse(linkTypes);
  }
}
