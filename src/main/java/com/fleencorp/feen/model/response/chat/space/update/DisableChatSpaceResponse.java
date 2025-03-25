package com.fleencorp.feen.model.response.chat.space.update;

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
public class DisableChatSpaceResponse extends ApiResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("is_active_info")
  private IsActiveInfo isActiveInfo;

  @Override
  public String getMessageCode() {
    return "disable.chat.space";
  }

  public static DisableChatSpaceResponse of(final Long chatSpaceId, final IsActiveInfo isActiveInfo) {
    return new DisableChatSpaceResponse(chatSpaceId, isActiveInfo);
  }
}
