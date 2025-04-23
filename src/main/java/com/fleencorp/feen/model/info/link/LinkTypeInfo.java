package com.fleencorp.feen.model.info.link;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.link.LinkType;
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
  "format"
})
public class LinkTypeInfo {

  @JsonProperty("type")
  private LinkType type;

  @JsonProperty("label")
  private String label;

  @JsonProperty("format")
  private String format;

  public static LinkTypeInfo of(final LinkType type, final String label, final String format) {
    return new LinkTypeInfo(type, label, format);
  }
}
