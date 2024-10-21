package com.fleencorp.feen.model.response.notification;

import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ReadNotificationResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "read.notification";
  }

  public static ReadNotificationResponse of() {
    return new ReadNotificationResponse();
  }
}
