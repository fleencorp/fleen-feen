package com.fleencorp.feen.common.model.response.core;

import com.fasterxml.jackson.annotation.*;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static com.fleencorp.base.util.datetime.DateFormatUtil.DATE_TIME;
import static com.fleencorp.feen.common.constant.message.ResponseMessage.SUCCESS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

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

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "message",
    "total"
  })
  public static class CountAllResponse extends LocalizedResponse {

    @JsonProperty("total")
    private long total;

    @Override
    public String getMessageCode() {
      return "count.all";
    }

    public static CountAllResponse of(final long total) {
      return new CountAllResponse(total);
    }
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "message",
    "id",
    "status_code",
    "timestamp"
  })
  public static class DeleteResponse extends LocalizedResponse {

    @JsonProperty("id")
    private Object id;

    @JsonProperty("status_code")
    private Integer statusCode;

    @JsonFormat(shape = STRING, pattern = DATE_TIME)
    @JsonProperty("timestamp")
    private final String timestamp;

    @Override
    public String getMessageCode() {
      return "delete";
    }

    public DeleteResponse(final Object id) {
      this(SUCCESS, true);
      this.id = id;
    }

    public DeleteResponse(final String message, final boolean status) {
      super();
      this.message = message;
      this.timestamp = LocalDateTime.now().toString();
      this.statusCode = status ? OK.value() : BAD_REQUEST.value();
    }

    public DeleteResponse() {
      this(SUCCESS, true);
    }

    public static DeleteResponse of() {
      return new DeleteResponse();
    }

    public static DeleteResponse of(final Object id) {
      return new DeleteResponse(id);
    }
  }

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
  public static class EntityExistsResponse extends LocalizedResponse {

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
}
