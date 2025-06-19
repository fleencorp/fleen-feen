package com.fleencorp.feen.block.user.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.block.user.model.info.HasBlockedInfo;
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
  "full_name",
  "username",
  "has_blocked_info"
})
public class BlockUserResponse {

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("username")
  private String username;

  @JsonProperty("has_blocked_info")
  private HasBlockedInfo hasBlockedInfo;

  public static BlockUserResponse of(final String fullName, final String username, final HasBlockedInfo hasBlockedInfo) {
    return new BlockUserResponse(fullName, username, hasBlockedInfo);
  }
}
