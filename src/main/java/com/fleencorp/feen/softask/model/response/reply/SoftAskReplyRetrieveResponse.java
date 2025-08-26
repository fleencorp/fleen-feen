package com.fleencorp.feen.softask.model.response.reply;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
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
  "soft_ask_reply"
})
public class SoftAskReplyRetrieveResponse extends LocalizedResponse {

  @JsonProperty("soft_ask_reply_id")
  private Long softAskReplyId;

  @JsonProperty("soft_ask_reply")
  private SoftAskReplyResponse softAskReply;

  @Override
  public String getMessageCode() {
    return "soft.ask.reply.retrieve";
  }

  public static SoftAskReplyRetrieveResponse of(final Long softAskReplyId, final SoftAskReplyResponse softAskReply) {
    return new SoftAskReplyRetrieveResponse(softAskReplyId, softAskReply);
  }

}
