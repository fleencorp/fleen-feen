package com.fleencorp.feen.softask.model.response.reply;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
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
  "soft_ask_reply_id",
  "is_deleted_info"
})
public class SoftAskReplyDeleteResponse extends LocalizedResponse {

  @JsonProperty("soft_ask_reply_id")
  private Long softAskReplyId;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "soft.ask.reply.delete";
  }

  public static SoftAskReplyDeleteResponse of(final Long softAskReplyId, final IsDeletedInfo deletedInfo) {
    return new SoftAskReplyDeleteResponse(softAskReplyId, deletedInfo);
  }
}