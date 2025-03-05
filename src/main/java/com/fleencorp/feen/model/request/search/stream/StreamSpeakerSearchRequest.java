package com.fleencorp.feen.model.request.search.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamSpeakerSearchRequest extends SearchRequest {

  @JsonProperty("q")
  protected String userIdOrName;

  public void setDefaultPageSize() {
    setPageSize(100);
  }
}
