package com.fleencorp.feen.model.response.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CountAllResponse {

  @JsonProperty("total")
  private long total;

  public static CountAllResponse of(final long total) {
    return CountAllResponse.builder()
            .total(total)
            .build();
  }
}
