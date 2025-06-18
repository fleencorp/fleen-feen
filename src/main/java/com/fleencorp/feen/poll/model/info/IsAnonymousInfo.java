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
  "anonymous",
  "anonymous_text",
  "anonymous_other_text"
})
public class IsAnonymousInfo {

  @JsonProperty("is_anonymous")
  private Boolean anonymous;

  @JsonProperty("anonymous_text")
  private String anonymousText;

  @JsonProperty("anonymous_other_text")
  private String anonymousOtherText;

  public static IsAnonymousInfo of(final Boolean anonymous, final String anonymousText, final String anonymousOtherText) {
    return new IsAnonymousInfo(anonymous, anonymousText, anonymousOtherText);
  }
}
