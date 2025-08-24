package com.fleencorp.feen.link.model.dto.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.link.constant.LinkParentType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class BaseLinkDto {

  @IsNumber(message = "{link.parentId.IsNumber}")
  @JsonProperty("parent_id")
  protected String parentId;

  @NotNull(message = "{link.parentLinkType.NotNull}")
  @OneOf(enumClass = LinkParentType.class, message = "{link.parentLinkType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("parent_link_type")
  protected String parentLinkType;

  public Long getParentId() {
    return hasParent() ? Long.parseLong(parentId) : null;
  }

  public LinkParentType getParentLinkType() {
    return LinkParentType.of(parentLinkType);
  }

  public Long getStreamId() {
    return hasParent() && LinkParentType.isStream(getParentLinkType())? Long.parseLong(parentId) : null;
  }

  public boolean hasParent() {
    return parentId != null;
  }
}

