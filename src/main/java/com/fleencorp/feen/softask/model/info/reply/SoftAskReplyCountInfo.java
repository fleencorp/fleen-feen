package com.fleencorp.feen.softask.model.info.reply;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "reply_count",
  "reply_count_text",
})
public class SoftAskReplyCountInfo {

  @JsonProperty("reply_count")
  private Integer replyCount;

  @JsonProperty("reply_count_text")
  private String replyCountText;

  public static SoftAskReplyCountInfo of(final Integer replyCount, final String replyCountText) {
    return new SoftAskReplyCountInfo(replyCount, replyCountText);
  }
}
