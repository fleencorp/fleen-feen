package com.fleencorp.feen.block.user.model.info;

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
  "blocked",
  "blocked_text",
  "blocked_text_2",
  "blocked_other_text"
})
public class HasBlockedInfo {

  @JsonProperty("blocked")
  private Boolean blocked;

  @JsonProperty("blocked_text")
  private String blockedText;

  @JsonProperty("blocked_text_2")
  private String blockedText2;

  @JsonProperty("blocked_other_text")
  private String blockedOtherText;

  public static HasBlockedInfo of(final Boolean blocked, final String blockedText, final String blockedText2, final String blockedOtherText) {
    return new HasBlockedInfo(blocked, blockedText, blockedText2, blockedOtherText);
  }
}
