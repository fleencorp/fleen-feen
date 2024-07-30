package com.fleencorp.feen.model.response.share;

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
  "full_name",
  "user_id"
})
public class BlockedUserResponse {

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("user_id")
  private Long userId;

  public static BlockedUserResponse of(final String fullName, final Long userId) {
    return BlockedUserResponse.builder()
        .fullName(fullName)
        .userId(userId)
        .build();
  }
}
