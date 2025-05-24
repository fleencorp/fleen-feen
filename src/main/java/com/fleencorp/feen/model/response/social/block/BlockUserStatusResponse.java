package com.fleencorp.feen.model.response.social.block;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.social.BlockStatus;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "block_status"
})
public class BlockUserStatusResponse extends LocalizedResponse {

  @JsonFormat(shape = STRING)
  @JsonProperty("block_status")
  private BlockStatus blockStatus;

  @Override
  public String getMessageCode() {
    return BlockStatus.isBlocked(blockStatus)
      ? "block.user.status.blocked"
      : "block.user.status.unblocked";
  }

  @Override
  public Object[] getParams() {
    return new Object[] { blockStatus };
  }

  public static BlockUserStatusResponse of(final BlockStatus blockStatus) {
    return new BlockUserStatusResponse(blockStatus);
  }
}
