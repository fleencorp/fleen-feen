package com.fleencorp.feen.chat.space.model.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceRequestToJoinPendingSelect {

 private Long chatSpaceId;
 private Long requestToJoinTotal;
}
