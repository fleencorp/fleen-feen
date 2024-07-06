package com.fleencorp.feen.model.dto.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteIdsDto {

  @NotEmpty(message = "{entity.ids.NotEmpty}")
  @JsonProperty("ids")
  private List<Long> ids;
}
