package com.fleencorp.feen.model.response.base;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;

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

  @JsonIgnore
  public Long getNumberId() {
    return (Long) id;
  }

  public FleenFeenResponse(final Object id) {
    this.id = id;
  }

  public static FleenFeenResponse of() {
    return new FleenFeenResponse();
  }

  public static FleenFeenResponse of(final Object id) {
    return new FleenFeenResponse(id);
  }
}
