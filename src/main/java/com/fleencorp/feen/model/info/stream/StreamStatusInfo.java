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
    return StreamStatusInfo.builder()
      .streamStatus(streamStatus)
      .streamStatusText(streamStatusText)
      .streamStatusText2(streamStatusText2)
      .streamStatusText3(streamStatusText3)
      .build();
  }
}

