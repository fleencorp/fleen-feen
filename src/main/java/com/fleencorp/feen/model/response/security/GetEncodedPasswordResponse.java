package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.util.datetime.DateFormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({
  "encoded_password",
  "raw_password",
  "timestamp"
})
public class GetEncodedPasswordResponse {

  @JsonProperty("encoded_password")
  private String encodedPassword;

  @JsonProperty("raw_password")
  private String rawPassword;

  @JsonFormat(shape = STRING, pattern = DateFormatUtil.DATE_TIME)
  @JsonProperty("timestamp")
  private LocalDateTime timestamp;

  public GetEncodedPasswordResponse(final String encodedPassword, final String rawPassword) {
    this.encodedPassword = encodedPassword;
    this.rawPassword = rawPassword;
    this.timestamp = LocalDateTime.now();
  }

  public static GetEncodedPasswordResponse of(final String encodedPassword, final String rawPassword) {
    return new GetEncodedPasswordResponse(encodedPassword, rawPassword);
  }
}
