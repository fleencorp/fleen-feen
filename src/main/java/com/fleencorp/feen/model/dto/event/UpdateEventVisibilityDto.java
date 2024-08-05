package com.fleencorp.feen.model.dto.event;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.converter.common.ToUpperCase;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class UpdateEventVisibilityDto {

  @NotNull(message = "{stream.visibility.NotNull}")
  @ValidEnum(enumClass = StreamVisibility.class, message = "{stream.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  protected String visibility;

  public StreamVisibility getActualVisibility() {
    return parseEnumOrNull(visibility, StreamVisibility.class);
  }
}
