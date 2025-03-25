package com.fleencorp.feen.model.info.chat.space;

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
  "active",
  "active_text",
  "active_text_2"
})
public class IsActiveInfo {

  @JsonProperty("active")
  private Boolean active;

  @JsonProperty("active_text")
  private String activeText;

  @JsonProperty("active_text_2")
  private String activeText2;

  public static IsActiveInfo of(final Boolean active, final String activeText, final String activeText2) {
    return new IsActiveInfo(active, activeText, activeText2);
  }
}
