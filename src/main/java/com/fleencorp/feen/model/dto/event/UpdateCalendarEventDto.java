package com.fleencorp.feen.model.dto.event;

import com.fleencorp.feen.model.dto.stream.UpdateStreamDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class UpdateCalendarEventDto extends UpdateStreamDto {

}
