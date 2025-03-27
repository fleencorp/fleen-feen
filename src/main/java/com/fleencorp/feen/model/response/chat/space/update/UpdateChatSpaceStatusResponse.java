package com.fleencorp.feen.model.response.chat.space.update;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.chat.space.IsActiveInfo;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "chat_space_id",
  "is_active_info"
})
public class UpdateChatSpaceStatusResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("is_active_info")
  private IsActiveInfo isActiveInfo;

  @JsonIgnore
  private boolean isActive() {
    return isActiveInfo != null ? isActiveInfo.getActive() : false;
  }

  @Override
  public String getMessageCode() {
    return isActive() ? "enable.chat.space" : "disable.chat.space";
  }

  public static UpdateChatSpaceStatusResponse of(final Long chatSpaceId, final IsActiveInfo isActiveInfo) {
    return new UpdateChatSpaceStatusResponse(chatSpaceId, isActiveInfo);
  }
}
