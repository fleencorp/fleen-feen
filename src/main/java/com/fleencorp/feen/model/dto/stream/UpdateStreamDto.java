package com.fleencorp.feen.model.dto.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
public class UpdateStreamDto {

  @NotBlank(message = "{stream.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{stream.title.Size}")
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{stream.description.NotBlank}")
  @Size(max = 1000, message = "{stream.description.Size}")
  @JsonProperty("description")
  private String description;

  @NotEmpty(message = "{stream.tags.NotBlank}")
  @Size(min = 1, max = 300, message = "{stream.tags.Size}")
  @ToLowerCase
  @JsonProperty("tags")
  protected String tags;

  @NotBlank(message = "{stream.location.NotBlank}")
  @Size(max = 50, message = "{stream.location.Size}")
  @ToTitleCase
  @JsonProperty("location")
  protected String location;
}
