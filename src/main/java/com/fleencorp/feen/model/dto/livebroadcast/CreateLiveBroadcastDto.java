package com.fleencorp.feen.model.dto.livebroadcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.stream.CreateStreamDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.stream.StreamCreationType.SCHEDULED;
import static com.fleencorp.feen.constant.stream.StreamStatus.ACTIVE;
import static com.fleencorp.feen.converter.impl.ToTitleCaseConverter.toTitleCase;
import static java.lang.Boolean.parseBoolean;

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

  public FleenStream toFleenStream() {
    return FleenStream.builder()
            .title(title)
            .description(description)
            .tags(tags)
            .location(location)
            .timezone(toTitleCase(timezone))
            .scheduledStartDate(startDateTime)
            .scheduledEndDate(endDateTime)
            .streamType(getActualType())
            .streamVisibility(getActualVisibility())
            .streamCreationType(SCHEDULED)
            .streamStatus(ACTIVE)
            .forKids(parseBoolean(isForKids))
            .build();
  }
}
