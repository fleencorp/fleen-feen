package com.fleencorp.feen.model.info.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamStatus;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "stream_status",
  "stream_status_text"
})
public class StreamStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_status")
  private StreamStatus streamStatus;

  @JsonProperty("stream_status_text")
  private String streamStatusText;

  public static StreamStatusInfo of(final StreamStatus streamStatus, final String streamStatusText) {
    return StreamStatusInfo.builder()
      .streamStatus(streamStatus)
      .streamStatusText(streamStatusText)
      .build();
  }
}

