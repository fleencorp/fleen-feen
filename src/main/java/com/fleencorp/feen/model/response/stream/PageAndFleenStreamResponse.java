package com.fleencorp.feen.model.response.stream;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageAndFleenStreamResponse {

  private List<FleenStreamResponse> responses;
  private Page<?> page;

  public static PageAndFleenStreamResponse of(final List<FleenStreamResponse> responses, final Page<?> page) {
    return PageAndFleenStreamResponse.builder()
      .responses(responses)
      .page(page)
      .build();
  }
}
