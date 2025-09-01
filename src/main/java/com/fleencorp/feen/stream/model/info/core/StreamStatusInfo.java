package com.fleencorp.feen.stream.model.info.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.stream.constant.core.StreamStatus;
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
  "stream_status",
  "stream_status_text",
  "stream_status_text_2",
  "stream_status_text_3"
})
public class StreamStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_status")
  private StreamStatus streamStatus;

  @JsonProperty("stream_status_text")
  private String streamStatusText;

  @JsonProperty("stream_status_text_2")
  private String streamStatusText2;

  @JsonProperty("stream_status_text_3")
  private String streamStatusText3;

  public static StreamStatusInfo of(final StreamStatus streamStatus, final String streamStatusText, final String streamStatusText2, final String streamStatusText3) {
    return new StreamStatusInfo(streamStatus, streamStatusText, streamStatusText2, streamStatusText3);
  }

  public static StreamStatusInfo of() {
    return new StreamStatusInfo();
  }
}

