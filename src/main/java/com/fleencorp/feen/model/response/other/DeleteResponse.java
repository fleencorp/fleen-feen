package com.fleencorp.feen.model.response.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static com.fleencorp.feen.constant.message.ResponseMessage.SUCCESS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class DeleteResponse {

  @JsonProperty("id")
  private Object id;

  @JsonProperty("message")
  private final String message;

  @JsonProperty("status_code")
  private Integer statusCode;

  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  private final String timestamp;

  public DeleteResponse(Object id) {
    this(SUCCESS, true);
    this.id = id;
  }

  public DeleteResponse(String message, boolean status) {
    this.message = message;
    this.timestamp = LocalDateTime.now().toString();
    this.statusCode = status ? OK.value() : BAD_REQUEST.value();
  }

  public DeleteResponse() {
    this(SUCCESS, true);
  }
}
