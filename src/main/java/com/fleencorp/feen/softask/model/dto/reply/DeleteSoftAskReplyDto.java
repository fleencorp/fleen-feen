package com.fleencorp.feen.softask.model.dto.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSoftAskReplyDto {

  private Long softAskReplyId;

  public static DeleteSoftAskReplyDto of(final Long softAskReplyId) {
    return new DeleteSoftAskReplyDto(softAskReplyId);
  }
}
