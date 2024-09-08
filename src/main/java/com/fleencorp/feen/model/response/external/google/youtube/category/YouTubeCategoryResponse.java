package com.fleencorp.feen.model.response.external.google.youtube.category;


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
  "kind",
  "id",
  "name"
})
public class YouTubeCategoryResponse {

  @JsonProperty("kind")
  private String kind;

  @JsonProperty("id")
  private String id;

  @JsonProperty("name")
  private String name;

  public static YouTubeCategoryResponse of() {
    return new YouTubeCategoryResponse();
  }
}
