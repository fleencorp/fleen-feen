package com.fleencorp.feen.model.dto.livebroadcast;

import com.fleencorp.feen.constant.external.google.oauth2.Oauth2ServiceType;
import com.fleencorp.feen.model.dto.stream.RescheduleStreamDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class RescheduleLiveBroadcastDto extends RescheduleStreamDto {

  public Oauth2ServiceType getOauth2ServiceType() {
    return Oauth2ServiceType.youTube();
  }
}
