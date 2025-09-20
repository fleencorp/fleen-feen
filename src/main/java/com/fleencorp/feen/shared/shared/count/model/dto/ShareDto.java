package com.fleencorp.feen.shared.shared.count.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.shared.shared.count.constant.ShareCountParentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareDto {

  @NotNull(message = "{share.count.parent.NotNull}")
  @JsonProperty("parent")
  private ShareParentDto parent;

  private boolean hasParent() {
    return nonNull(parent);
  }

  public Long getParentId() {
    return hasParent() ? parent.getParentId() : null;
  }

  public ShareCountParentType getShareCountParentType() {
    return hasParent() ? parent.getShareCountParentType() : null;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class ShareParentDto {

    @NotNull(message = "{share.count.parentType.NotNull}")
    @OneOf(enumClass = ShareCountParentType.class, message = "{share.count.parentType.Type}")
    @JsonProperty("parent_type")
    private String shareCountParentType;

    @NotNull(message = "{share.count.parentId.NotNull}")
    @IsNumber(message = "{share.count.parentId.IsNumber}")
    @JsonProperty("parent_id")
    protected String parentId;

    @IsNumber(message = "{share.count.otherId.IsNumber}")
    @JsonProperty("other_id")
    protected String otherId;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public ShareCountParentType getShareCountParentType() {
      return ShareCountParentType.of(shareCountParentType);
    }
  }
}
