package com.fleencorp.feen.model.response.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Getter
@AllArgsConstructor
public class EntityExistsResponse {

  private boolean exists;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  private LocalDateTime timestamp;

  @JsonProperty("status_code")
  private Integer statusCode;

  public EntityExistsResponse(final boolean exists) {
    this(exists, true);
  }

  public EntityExistsResponse(final boolean exists, final boolean status) {
    this.exists = exists;
    this.timestamp = LocalDateTime.now();
    this.statusCode = status ? OK.value() : BAD_REQUEST.value();
  }
}
