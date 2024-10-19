package com.fleencorp.feen.model.dto.chat.member;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinChatSpaceDto {

  @Size(min = 10, max = 500, message = "{comment.Size}")
  @JsonProperty("comment")
  protected String comment;
}
