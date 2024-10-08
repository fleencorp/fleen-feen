package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinEventOrStreamDto {

  @Size(min = 10, max = 500, message = "{event.comment.Size}")
  @JsonProperty("comment")
  protected String comment;
}
