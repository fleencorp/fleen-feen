package com.fleencorp.feen.link.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.link.constant.LinkType;
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
  "type",
  "label",
  "business_format",
  "community_format"
})
public class LinkTypeInfo {

  @JsonProperty("type")
  private LinkType type;

  @JsonProperty("label")
  private String label;

  @JsonProperty("business_format")
  private String businessFormat;

  @JsonProperty("community_format")
  private String communityFormat;

  public static LinkTypeInfo of(final LinkType type, final String label, final String businessFormat, final String communityFormat) {
    return new LinkTypeInfo(type, label, businessFormat, communityFormat);
  }
}
