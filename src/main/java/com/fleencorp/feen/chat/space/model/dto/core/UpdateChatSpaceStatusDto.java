package com.fleencorp.feen.chat.space.model.dto.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateChatSpaceStatusDto {

  @NotNull(message = "{chatSpace.status.NotNull}")
  @OneOf(enumClass = ChatSpaceStatus.class, message = "{chatSpace.status.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("status")
  private String status;

  public boolean getStatus() {
    final ChatSpaceStatus chatSpaceStatus = ChatSpaceStatus.of(status);
    return ChatSpaceStatus.isActive(chatSpaceStatus);
  }
}
