package com.fleencorp.feen.business.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.business.constant.BusinessStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BusinessSearchRequest extends SearchRequest {

  @JsonProperty("title")
  private String title;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private BusinessStatus status;

  public boolean hasTitle() {
    return title != null && !title.isEmpty();
  }
}
