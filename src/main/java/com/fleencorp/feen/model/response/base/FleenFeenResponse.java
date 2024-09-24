package com.fleencorp.feen.model.response.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "message",
  "created_on",
  "updated_on"
})
public class FleenFeenResponse {

  @JsonProperty("id")
  protected Object id;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("created_on")
  protected LocalDateTime createdOn;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  @JsonProperty("updated_on")
  protected LocalDateTime updatedOn;

  public Long getNumberId() {
    return (Long) id;
  }

  public FleenFeenResponse(final Object id) {
    this.id = id;
  }

  public static FleenFeenResponse of() {
    return FleenFeenResponse.builder()
        .build();
  }

  public static FleenFeenResponse of(final Object id) {
    return new FleenFeenResponse(id);
  }
}
