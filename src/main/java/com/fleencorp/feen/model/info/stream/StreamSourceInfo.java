package com.fleencorp.feen.model.info.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "stream_source",
  "stream_source_text"
})
public class StreamSourceInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_source")
  private StreamSource streamSource;

  @JsonProperty("stream_source_text")
  private String streamSourceText;

  public static StreamSourceInfo of(final StreamSource streamSource, final String streamSourceText) {
    return new StreamSourceInfo(streamSource, streamSourceText);
  }
}

