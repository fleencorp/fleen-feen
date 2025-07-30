package com.fleencorp.feen.stream.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class StreamResponsesAndPage {

  private List<StreamResponse> responses;
  private Page<?> page;

  public static StreamResponsesAndPage of(final List<StreamResponse> responses, final Page<?> page) {
    return new StreamResponsesAndPage(responses, page);
  }
}
