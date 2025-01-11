package com.fleencorp.feen.model.info.stream;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamType;
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
  "stream_type",
  "stream_type_text"
})
public class StreamTypeInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("stream_type")
  private StreamType streamType;

  @JsonProperty("stream_type_text")
  private String streamTypeText;

  public static StreamTypeInfo of(final StreamType streamType, final String streamTypeText) {
    return new StreamTypeInfo(streamType, streamTypeText);
  }
}

