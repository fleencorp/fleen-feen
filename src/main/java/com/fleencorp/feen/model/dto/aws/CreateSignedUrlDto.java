package com.fleencorp.feen.model.dto.aws;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.common.constant.file.ObjectTypeUpload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSignedUrlDto {

  @NotNull(message = "{fileNames.NotNull}")
  @NotEmpty(message = "{fileNames.NotEmpty}")
  @JsonProperty("file_names")
  private List<String> fileNames;

  @JsonProperty
  private ObjectTypeUpload objectType;

  public void profilePhoto() {
    objectType = ObjectTypeUpload.profilePhoto();
  }

  public void streamCoverPhoto() {
    objectType = ObjectTypeUpload.streamCoverPhoto();
  }

  public List<String> getAllFileNames() {
    if (nonNull(fileNames)) {
      return fileNames.stream()
        .filter(Objects::nonNull)
        .toList();
    }
    return List.of();
  }
}
