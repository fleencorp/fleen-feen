package com.fleencorp.feen.model.request.youtube.broadcast.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveBroadcastRequest {

  private String accessTokenForHttpRequest;
}
