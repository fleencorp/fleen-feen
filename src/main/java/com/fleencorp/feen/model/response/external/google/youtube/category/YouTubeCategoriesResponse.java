package com.fleencorp.feen.model.response.external.google.youtube.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "categories"
})
public class YouTubeCategoriesResponse {

  private List<YouTubeCategoryResponse> categories;

  public static YouTubeCategoriesResponse of(final List<YouTubeCategoryResponse> categories) {
    return YouTubeCategoriesResponse.builder()
        .categories(categories)
        .build();
  }

  public boolean hasCategories() {
    return categories != null && !categories.isEmpty();
  }
}
