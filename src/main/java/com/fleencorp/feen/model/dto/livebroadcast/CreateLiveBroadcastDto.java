package com.fleencorp.feen.model.dto.livebroadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidBoolean;
import com.fleencorp.feen.model.dto.stream.CreateStreamDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLiveBroadcastDto extends CreateStreamDto {

  @NotBlank(message = "{liveBroadcast.thumbnailUrl.NotBlank}")
  @Size(min = 1, max = 1000, message = "{liveBroadcast.thumbnailUrl.Size}")
  @JsonProperty("thumbnail_link_or_url")
  private String thumbnailUrl;

  @NotNull(message = "{liveBroadcast.madeForKids.NotNull}")
  @ValidBoolean
  @JsonProperty("made_for_kids")
  private Boolean madeForKids;
}
