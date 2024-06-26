package com.fleencorp.feen.model.dto.event;

import com.fleencorp.feen.model.dto.stream.RescheduleStreamDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class RescheduleCalendarEventDto extends RescheduleStreamDto {
  
}
