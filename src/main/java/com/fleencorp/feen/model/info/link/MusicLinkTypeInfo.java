package com.fleencorp.feen.model.info.link;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.common.MusicLinkType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

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
public class MusicLinkTypeInfo {

  @JsonProperty("type")
  private MusicLinkType type;

  @JsonProperty("label")
  private String label;

  @JsonProperty("format")
  private String format;

  public static MusicLinkTypeInfo of(final MusicLinkType type, final String label, final String format) {
    return new MusicLinkTypeInfo(type, label, format);
  }

  public static MusicLinkTypeInfo of(final MusicLinkType type) {
    if (nonNull(type)) {
      return of(type, type.getValue(), type.getFormat());
    }

    return null;
  }
}
