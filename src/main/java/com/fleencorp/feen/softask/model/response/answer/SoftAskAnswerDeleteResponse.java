package com.fleencorp.feen.softask.model.response.answer;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  "soft_ask_answer_id",
  "is_deleted_info"
})
public class SoftAskAnswerDeleteResponse extends LocalizedResponse {

  @JsonProperty("soft_ask_answer_id")
  private Long softAskAnswerId;

  @JsonProperty("is_deleted_info")
  private IsDeletedInfo deletedInfo;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return "soft.ask.answer.delete";
  }

  public static SoftAskAnswerDeleteResponse of(final Long softAskAnswerId, final IsDeletedInfo deletedInfo) {
    return new SoftAskAnswerDeleteResponse(softAskAnswerId, deletedInfo);
  }
}