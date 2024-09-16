package com.fleencorp.feen.model.response.broadcast;

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
  "total_count"
})
public class TotalStreamsCreatedByUserResponse {

  @JsonProperty("total_count")
  private Long totalCount;

  public static TotalStreamsCreatedByUserResponse of(final Long totalCount) {
    return TotalStreamsCreatedByUserResponse.builder()
        .totalCount(totalCount)
        .build();
  }
}
