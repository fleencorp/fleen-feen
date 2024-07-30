package com.fleencorp.feen.model.response.share;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.share.BlockStatus;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "block_status"
})
public class BlockUserStatusResponse {

  @JsonProperty("message")
  private String message;

  @JsonProperty("block_status")
  private BlockStatus blockStatus;

  public static BlockUserStatusResponse of(final BlockStatus blockStatus) {
    final String message = String.format("user %s successfully", blockStatus.getValue());

    return BlockUserStatusResponse.builder()
        .message(message)
        .blockStatus(blockStatus)
        .build();
  }
}
