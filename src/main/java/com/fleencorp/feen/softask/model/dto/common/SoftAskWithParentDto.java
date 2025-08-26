package com.fleencorp.feen.softask.model.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
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
public class SoftAskWithParentDto {

  @NotNull(message = "{softAskReply.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskParentDto parent;

  protected boolean hasParent() {
    return nonNull(parent);
  }

  public Long getSoftAskParentReplyId() {
    return hasParent() ? parent.getSoftAskReplyId() : null;
  }

  public Long getSoftAskReplyId() {
    return hasParent() ? parent.getSoftAskReplyId() : null;
  }

  public Long getSoftAskId() {
    return hasParent() ? parent.getSoftAskId() : null;
  }

  public boolean hasSoftAskParentReply() {
    return hasParent() && nonNull(getSoftAskParentReplyId());
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskParentDto {

    @NotNull(message = "{softAskReply.parentId.NotNull}")
    @IsNumber(message = "{softAskReply.parentId.IsNumber}")
    @JsonProperty(value = "soft_ask_id")
    private String softAskId;

    @IsNumber(message = "{softAskReply.parentId.IsNumber}")
    @JsonProperty(value = "soft_ask_reply_id")
    private String softAskReplyId;

    public Long getSoftAskReplyId() {
      return nonNull(softAskReplyId) ? Long.parseLong(softAskReplyId) : null;
    }

    public Long getSoftAskId() {
      return nonNull(softAskId) ? Long.parseLong(softAskId) : null;
    }
  }
}
