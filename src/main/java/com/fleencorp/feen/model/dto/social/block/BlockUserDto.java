package com.fleencorp.feen.model.dto.social.block;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.social.BlockStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @IsNumber
  @JsonProperty("recipient_id")
  private String recipientId;

  @NotNull(message = "{share.blockStatus.NotNull}")
  @ValidEnum(enumClass = BlockStatus.class, message = "{share.blockStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("block_status")
  private String blockStatus;

  public BlockStatus getActualBlockStatus() {
    return BlockStatus.of(blockStatus);
  }

  public Long getActualRecipientId() {
    return Long.parseLong(recipientId);
  }
}
