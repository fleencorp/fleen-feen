package com.fleencorp.feen.model.response.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "exists",
  "timestamp"
})
public class EntityExistsResponse extends ApiResponse {

  @JsonProperty("exists")
  protected boolean exists;

  @JsonProperty("timestamp")
  @JsonFormat(shape = STRING, pattern = DATE_TIME)
  protected LocalDateTime timestamp;

  @JsonProperty("status_code")
  protected Integer statusCode;

  protected static Integer getActualStatusCode(final boolean status) {
    return status ? OK.value() : BAD_REQUEST.value();
  }

  @Override
  public String getMessageCode() {
    return "";
  }

  public EntityExistsResponse(final boolean exists) {
    this(exists, true);
  }

  public EntityExistsResponse(final boolean exists, final boolean status) {
    super();
    this.exists = exists;
    this.timestamp = LocalDateTime.now();
    this.statusCode = status ? OK.value() : BAD_REQUEST.value();
  }
}
