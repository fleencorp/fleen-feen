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
  "id",
  "answer_reply_count",
  "reply",
  "created_on",
  "updated_on"
})
public class SoftAskReplyAddResponse extends LocalizedResponse {

  @JsonProperty("answer_reply_count")
  private Integer answerReplyCount;

  @JsonProperty("reply")
  private SoftAskReplyResponse softAskReplyResponse;

  @Override
  public String getMessageCode() {
    return "soft.ask.reply.add";
  }

  public static SoftAskReplyAddResponse of(final Integer replyCount, final SoftAskReplyResponse softAskReplyResponse) {
    return new SoftAskReplyAddResponse(replyCount, softAskReplyResponse);
  }
}
