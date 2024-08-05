package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fleencorp.base.util.datetime.DateFormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@AllArgsConstructor
public class GetEncodedPasswordResponse {

  private String encodedPassword;
  private String rawPassword;

  @JsonFormat(shape = STRING, pattern = DateFormatUtil.DATE_TIME)
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
