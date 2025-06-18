package com.fleencorp.feen.poll.model.dto;

import com.fleencorp.feen.poll.model.domain.PollOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class UpdatePollDto extends AddPollDto {

  public Collection<PollOption> getPollOptions() {
    return toPollOptions();
  }
}
