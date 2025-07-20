package com.fleencorp.feen.softask.model.info;

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
  "answer_count",
  "answer_count_text",
  "answer_count_text_2"
})
public class SoftAskAnswerCountInfo {

  @JsonProperty("answer_count")
  private Integer answerCount;

  @JsonProperty("answer_count_text")
  private String answerCountText;

  @JsonProperty("answer_count_text_2")
  private String answerCountText2;

  public static SoftAskAnswerCountInfo of(final Integer answerCount, final String answerCountText, final String answerCountText2) {
    return new SoftAskAnswerCountInfo(answerCount, answerCountText, answerCountText2);
  }
}
