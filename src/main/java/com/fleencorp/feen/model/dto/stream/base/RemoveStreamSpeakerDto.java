package com.fleencorp.feen.model.dto.stream.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.util.FleenUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemoveStreamSpeakerDto {

  @Valid
  @NotNull(message = "{speaker.speakers.NotNull}")
  @Size(max = 10, message = "{speaker.speakers.Size}")
  @JsonProperty("speakers")
  private List<DeleteSpeakerDto> speakers;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeleteSpeakerDto {

    @JsonProperty("speaker_id")
    private String speakerId;
  }

  /**
   * Converts the list of {@code DeleteSpeakerDto} objects into a set of valid speaker IDs.
   *
   * <p>This method processes a list of speaker DTOs, extracts the speaker ID as a string,
   * filters out any invalid IDs using {@code FleenUtil::isValidNumber}, and then converts
   * the valid string IDs into a {@code Set<Long>}.</p>
   *
   * @return a set of valid speaker IDs as {@code Long} values
   */
  public Set<Long> toSpeakerIds() {
    return speakers.stream()
      .map(DeleteSpeakerDto::getSpeakerId)
      .filter(FleenUtil::isValidNumber)
      .map(Long::parseLong)
      .collect(Collectors.toSet());
  }
}
