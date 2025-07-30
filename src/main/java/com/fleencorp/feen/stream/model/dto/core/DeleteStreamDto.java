package com.fleencorp.feen.stream.model.dto.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.stream.constant.core.StreamType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteStreamDto {

  @NotNull(message = "{stream.streamType.NotNull}")
  @OneOf(enumClass = StreamType.class, message = "{stream.streamType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("stream_type")
  protected String streamType;

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isEvent() {
    return StreamType.isEvent(getStreamType());
  }
}
