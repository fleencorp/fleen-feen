package com.fleencorp.feen.poll.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletePollDto {

  private Long pollId;

  public static DeletePollDto of(final Long pollId) {
    return new DeletePollDto(pollId);
  }
}
