package com.fleencorp.feen.softask.model.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.softask.constant.core.SoftAskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSoftAskContentDto extends SoftAskWithParentDto {

  @NotBlank(message = "{softAskOther.content.NotBlank}")
  @Size(min = 10, max = 4000, message = "{softAskOther.content.Size}")
  @JsonProperty("content")
  private String content;

  @NotNull(message = "{softAskOther.type.NotNull}")
  @OneOf(enumClass = SoftAskType.class, message = "{softAskOther.type.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty(value = "type")
  private String type;

  public SoftAskType getSoftAskType() {
    return SoftAskType.of(type);
  }
}

