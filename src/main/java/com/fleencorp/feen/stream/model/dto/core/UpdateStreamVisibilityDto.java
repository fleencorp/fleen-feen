package com.fleencorp.feen.stream.model.dto.core;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class UpdateStreamVisibilityDto {

  @NotNull(message = "{stream.visibility.NotNull}")
  @OneOf(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  protected String visibility;

  @NotNull(message = "{stream.streamType.NotNull}")
  @OneOf(enumClass = StreamType.class, message = "{stream.streamType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("stream_type")
  protected String streamType;

  public StreamVisibility getActualVisibility() {
    return StreamVisibility.of(visibility);
  }

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public boolean isEvent() {
    return StreamType.isEvent(getStreamType());
  }
}
