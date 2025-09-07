package com.fleencorp.feen.block.user.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.common.constant.social.BlockStatus;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "user"
})
public class BlockUserStatusResponse extends LocalizedResponse {

  @JsonProperty("user")
  private BlockUserResponse blockUserResponse;

  @JsonIgnore
  private BlockStatus blockStatus;

  @Override
  @JsonIgnore
  public String getMessageCode() {
    return BlockStatus.isBlocked(blockStatus)
      ? "block.user.status.blocked"
      : "block.user.status.unblocked";
  }

  public static BlockUserStatusResponse of(final BlockUserResponse blockUserResponse, final BlockStatus blockStatus) {
    return new BlockUserStatusResponse(blockUserResponse, blockStatus);
  }
}
