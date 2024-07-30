package com.fleencorp.feen.model.dto.share;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.share.BlockStatus;
import com.fleencorp.feen.converter.common.ToUpperCase;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlockUserDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @Min(value = 0, message = "{share.recipient.Min}")
  @Max(value = Long.MAX_VALUE, message = "{share.recipient.Max}")
  @JsonProperty("recipient_id")
  private Long recipientId;

  @NotNull(message = "{share.blockStatus.NotNull}")
  @ValidEnum(enumClass = BlockStatus.class, message = "{share.blockStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("block_status")
  private String blockStatus;

  public BlockStatus getActualBlockStatus() {
    return parseEnumOrNull(blockStatus, BlockStatus.class);
  }
}
