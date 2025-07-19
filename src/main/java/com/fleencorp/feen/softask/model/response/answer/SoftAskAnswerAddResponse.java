package com.fleencorp.feen.softask.model.response.answer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.model.response.answer.core.SoftAskAnswerResponse;
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
  "answer_count",
  "answer"
})
public class SoftAskAnswerAddResponse extends LocalizedResponse {

  @JsonProperty("answer_count")
  private Integer answerCount;

  @JsonProperty("answer")
  private SoftAskAnswerResponse softAskAnswerResponse;

  @Override
  public String getMessageCode() {
    return "soft.ask.answer.add";
  }

  public static SoftAskAnswerAddResponse of(final Integer answerCount, final SoftAskAnswerResponse softAskAnswerResponse) {
    return new SoftAskAnswerAddResponse(answerCount, softAskAnswerResponse);
  }
}
