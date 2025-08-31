package com.fleencorp.feen.common.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "share_count",
  "share_count_text"
})
public class ShareCountInfo {

  @JsonProperty("share_count")
  private Integer shareCount;

  @JsonProperty("share_count_text")
  private String shareCountText;

  public static ShareCountInfo of(final Integer shareCount, final String shareCountText) {
    return new ShareCountInfo(shareCount, shareCountText);
  }
}
