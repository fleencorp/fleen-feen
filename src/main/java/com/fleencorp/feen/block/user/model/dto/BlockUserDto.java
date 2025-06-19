package com.fleencorp.feen.block.user.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.social.BlockStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @IsNumber
  @JsonProperty("user_id")
  private String userId;

  @NotNull(message = "{share.blockStatus.NotNull}")
  @OneOf(enumClass = BlockStatus.class, message = "{share.blockStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("status")
  private String status;

  public BlockStatus getStatus() {
    return BlockStatus.of(status);
  }

  public Long getUserId() {
    return Long.parseLong(userId);
  }
}
