package com.fleencorp.feen.model.view.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.util.datetime.DateFormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({
  "created_on",
  "updated_on"
})
public class FleenBaseView {

  @JsonFormat(shape = STRING, pattern = DateFormatUtil.DATE_TIME)
  @JsonProperty("created_on")
  private LocalDateTime createdOn;

  @JsonFormat(shape = STRING, pattern = DateFormatUtil.DATE_TIME)
  @JsonProperty("updated_on")
  private LocalDateTime updatedOn;
}
