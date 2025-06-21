package com.fleencorp.feen.poll.model.form.field;

import com.fasterxml.jackson.annotation.JsonInclude;
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
  "description"
})
public class PollFormFieldGuide {

  private String description;

  public static PollFormFieldGuide of(String description) {
    return new PollFormFieldGuide(description);
  }
}
