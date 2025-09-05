package com.fleencorp.feen.softask.model.dto.reply;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.softask.model.dto.common.SoftAskWithParentDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSoftAskReplyDto extends SoftAskWithParentDto {

  @NotBlank(message = "{softAskReply.content.NotBlank}")
  @Size(min = 10, max = 4000, message = "{softAskReply.content.Size}")
  @JsonProperty("reply")
  private String content;

}

