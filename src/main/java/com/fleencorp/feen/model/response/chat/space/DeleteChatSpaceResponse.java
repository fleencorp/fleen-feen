package com.fleencorp.feen.model.response.chat.space;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "is_deleted_info"
})
public class DeleteChatSpaceResponse extends LocalizedResponse {

  @JsonProperty("chat_space_id")
  private Long chatSpaceId;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @Override
  public String getMessageCode() {
    return "delete.chat.space";
  }

  public static DeleteChatSpaceResponse of(final Long chatSpaceId, final IsDeletedInfo deletedInfo) {
    return new DeleteChatSpaceResponse(chatSpaceId, deletedInfo);
  }
}
