package com.fleencorp.feen.model.info.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "visibility",
  "visibility_text"
})
public class StreamVisibilityInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private StreamVisibility visibility;

  @JsonProperty("visibility_text")
  private String visibilityText;

  public static StreamVisibilityInfo of(final StreamVisibility visibility, final String visibilityText) {
    return StreamVisibilityInfo.builder()
      .visibility(visibility)
      .visibilityText(visibilityText)
      .build();
  }
}

