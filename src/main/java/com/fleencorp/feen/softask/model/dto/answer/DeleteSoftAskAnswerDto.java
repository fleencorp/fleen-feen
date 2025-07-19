package com.fleencorp.feen.softask.model.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSoftAskAnswerDto {

  private Long softAskAnswerId;

  public static DeleteSoftAskAnswerDto of(final Long softAskAnswerId) {
    return new DeleteSoftAskAnswerDto(softAskAnswerId);
  }
}
