package com.fleencorp.feen.model.dto.stream.attendance;

import com.fleencorp.feen.constant.stream.StreamType;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotAttendingStreamDto {

  private StreamType streamType;
}
