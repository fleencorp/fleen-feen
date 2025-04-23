package com.fleencorp.feen.model.dto.link.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.link.ParentLinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.base.util.FleenUtil.isValidNumber;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseLinkDto {

  @IsNumber(message = "link.parentId.IsNumber")
  @JsonProperty("parent_id")
  protected String parentId;

  @NotNull(message = "{link.parentLinkType.NotNull}")
  @OneOf(enumClass = ParentLinkType.class, message = "{link.parentLinkType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("parent_link_type")
  protected String parentLinkType;

  public Long getChatSpaceId() {
    if (isValidNumber(parentId) && ParentLinkType.isChatSpace(parentLinkType)) {
      return Long.valueOf(parentId);
    }
    throw FailedOperationException.of();
  }

  public Long getStreamId() {
    if (isValidNumber(parentId) && ParentLinkType.isStream(parentLinkType)) {
      return Long.valueOf(parentId);
    }

    throw FailedOperationException.of();
  }
}

