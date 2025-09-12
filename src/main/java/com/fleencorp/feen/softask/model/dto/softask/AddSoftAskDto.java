package com.fleencorp.feen.softask.model.dto.softask;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.common.model.dto.UserOtherDetailDto;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSoftAskDto extends UserOtherDetailDto {

  @NotBlank(message = "{softAsk.question.NotBlank}")
  @Size(min = 1, max = 1500, message = "{softAsk.question.Size}")
  @JsonProperty("question")
  private String question;

  @NotNull(message = "{softAsk.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskParentDto parent;

  public boolean hasParent() {
    return nonNull(parent) && nonNull(parent.getParentId()) && nonNull(parent.getParentType());
  }

  public boolean hasNoParent() {
    return isNull(parent) || isNull(parent.getParentId());
  }

  public Long getParentId() {
    return nonNull(parent) ? parent.getParentId() : null;
  }

  public SoftAskParentType getParentType() {
    return hasParent() ? parent.getParentType() : null;
  }

  public boolean isChatSpaceParent() {
    return hasParent() && parent.isChatSpaceParent();
  }

  public boolean isPollParent() {
    return hasParent() && parent.isPollParent();
  }

  public boolean isStreamParent() {
    return hasParent() && parent.isStreamParent();
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskParentDto {

    @IsNumber(message = "{softAsk.parentId.IsNumber}")
    @JsonProperty(value = "parent_id")
    private String parentId = null;

    @OneOf(enumClass = SoftAskParentType.class, message = "{softAsk.parentType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty(value = "parent_type")
    private String parentType;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public SoftAskParentType getParentType() {
      return SoftAskParentType.of(parentType);
    }

    public boolean isChatSpaceParent() {
      return SoftAskParentType.isChatSpace(getParentType());
    }

    public boolean isPollParent() {
      return SoftAskParentType.isPoll(getParentType());
    }

    public boolean isStreamParent() {
      return SoftAskParentType.isStream(getParentType());
    }
  }
}
