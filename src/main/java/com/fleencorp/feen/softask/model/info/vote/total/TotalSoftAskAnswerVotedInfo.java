package com.fleencorp.feen.softask.model.info.vote.total;

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
  "total",
  "total_soft_ask_answer_voted_text",
})
public class TotalSoftAskAnswerVotedInfo {

  @JsonProperty("total")
  private Integer total;

  @JsonProperty("total_soft_ask_answer_voted_text")
  private String totalSoftAskAnswerVotedText;

  public static TotalSoftAskAnswerVotedInfo of(final Integer total, final String totalSoftAskAnswerVotedText) {
    return new TotalSoftAskAnswerVotedInfo(total, totalSoftAskAnswerVotedText);
  }
}
