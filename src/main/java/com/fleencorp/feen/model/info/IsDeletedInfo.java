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
  "deleted",
  "deleted_text",
  "deleted_text_2"
})
public class IsDeletedInfo {

  @JsonProperty("deleted")
  private Boolean deleted;

  @JsonProperty("deleted_text")
  private String deletedText;

  @JsonProperty("deleted_text_2")
  private String deletedText2;

  public static IsDeletedInfo of(final Boolean deleted, final String deletedText, final String deletedText2) {
    return IsDeletedInfo.builder()
      .deleted(deleted)
      .deletedText(deletedText)
      .deletedText2(deletedText2)
      .build();
  }
}
