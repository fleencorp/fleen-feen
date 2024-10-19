package com.fleencorp.feen.model.response.social.block;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.constant.social.BlockStatus;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

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
public class BlockUserStatusResponse extends ApiResponse {

  @JsonProperty("message")
  private String message;

  @JsonFormat(shape = STRING)
  @JsonProperty("block_status")
  private BlockStatus blockStatus;

  @Override
  public String getMessageCode() {
    return "block.user.status";
  }

  @JsonIgnore
  public Object[] getParams() {
    return new Object[] { blockStatus };
  }

  public static BlockUserStatusResponse of(final BlockStatus blockStatus) {
    return BlockUserStatusResponse.builder()
        .blockStatus(blockStatus)
        .build();
  }
}
