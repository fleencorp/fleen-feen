package com.fleencorp.feen.poll.model.form.field;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.model.form.PollFormField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "field",
  "description"
})
public class PollFormFieldGuide {

  @JsonFormat(shape = STRING)
  @JsonProperty("field")
  private PollFormField field;

  private String description;

  public static PollFormFieldGuide of(final PollFormField pollFormField, final String description) {
    return new PollFormFieldGuide(pollFormField, description);
  }
}
