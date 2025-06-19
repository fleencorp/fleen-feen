package com.fleencorp.feen.poll.model.response.base;

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
  "color",
  "percentage"
})
public class PollStatResponse {

  @JsonProperty("color")
  private String color;

  @JsonProperty("percentage")
  private double percentage;

  public static PollStatResponse of(final String color, final double percentage) {
    return new PollStatResponse(color, percentage);
  }
}

