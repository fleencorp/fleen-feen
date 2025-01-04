package com.fleencorp.feen.model.response.notification;

import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
