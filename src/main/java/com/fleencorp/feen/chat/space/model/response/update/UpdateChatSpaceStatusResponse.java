package com.fleencorp.feen.chat.space.model.response.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.model.info.core.ChatSpaceStatusInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "chat_space_id",
  "status_info"
})
public class UpdateChatSpaceStatusResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("status_info")
  private ChatSpaceStatusInfo statusInfo;

  @JsonIgnore
  private boolean isActive() {
    return nonNull(statusInfo.getStatus()) && ChatSpaceStatus.isActive(statusInfo.getStatus());
  }

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return isActive() ? "enable.chat.space" : "disable.chat.space";
  }

  public static UpdateChatSpaceStatusResponse of(final Long chatSpaceId, final ChatSpaceStatusInfo statusInfo) {
    return new UpdateChatSpaceStatusResponse(chatSpaceId, statusInfo);
  }
}
