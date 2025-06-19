package com.fleencorp.feen.poll.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.poll.constant.core.PollVisibility;
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
  "visibility",
  "visibility_text",
  "visibility_other_text"
})
public class PollVisibilityInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private PollVisibility visibility;

  @JsonProperty("visibility_text")
  private String visibilityText;

  @JsonProperty("visibility_other_text")
  private String visibilityOtherText;

  public static PollVisibilityInfo of(final PollVisibility visibility, final String visibilityText, final String visibilityOtherText) {
    return new PollVisibilityInfo(visibility, visibilityText, visibilityOtherText);
  }
}
