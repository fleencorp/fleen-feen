package com.fleencorp.feen.poll.model.info;

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
  "multiple_choice",
  "multiple_choice_text",
  "multiple_choice_other_text"
})
public class IsPollMultipleChoiceInfo {

  @JsonProperty("multiple_choice")
  private Boolean multipleChoice;

  @JsonProperty("multiple_choice_text")
  private String multipleChoiceText;

  @JsonProperty("multiple_choice_other_text")
  private String multipleChoiceOtherText;

  public static IsPollMultipleChoiceInfo of(final Boolean multipleChoice, final String multipleChoiceText, final String multipleChoiceOtherText) {
    return new IsPollMultipleChoiceInfo(multipleChoice, multipleChoiceText, multipleChoiceOtherText);
  }
}
