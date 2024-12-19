package com.fleencorp.feen.model.response.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StreamResponsesAndPage {

  private List<FleenStreamResponse> responses;
  private Page<?> page;

  public static StreamResponsesAndPage of(final List<FleenStreamResponse> responses, final Page<?> page) {
    return new StreamResponsesAndPage(responses, page);
  }
}
