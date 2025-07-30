package com.fleencorp.feen.common.model.info;

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
    return new IsDeletedInfo(deleted, deletedText, deletedText2);
  }
}
