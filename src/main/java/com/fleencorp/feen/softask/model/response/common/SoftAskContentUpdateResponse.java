package com.fleencorp.feen.softask.model.response.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "soft_ask_id",
  "soft_ask_reply_id"
})
public class SoftAskContentUpdateResponse extends LocalizedResponse {

  @JsonProperty("soft_ask_id")
  private Long softAskId;

  @JsonProperty("soft_ask_reply_id")
  private Long softAskReplyId;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "soft.ask.content.update";
  }

  public static SoftAskContentUpdateResponse of(final Long softAskId, final Long softAskReplyId) {
    return new SoftAskContentUpdateResponse(softAskId, softAskReplyId);
  }
}
