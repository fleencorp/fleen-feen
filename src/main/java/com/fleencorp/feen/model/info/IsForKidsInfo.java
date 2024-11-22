package com.fleencorp.feen.model.info;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "for_kids",
  "for_kids_text"
})
public class IsForKidsInfo {

  @JsonProperty("for_kids")
  private Boolean forKids;

  @JsonProperty("for_kids_text")
  private String forKidsText;

  public static IsForKidsInfo of(final Boolean isForKids, final String isFForKidsText) {
    return IsForKidsInfo.builder()
      .forKids(isForKids)
      .forKidsText(isFForKidsText)
      .build();
  }
}
