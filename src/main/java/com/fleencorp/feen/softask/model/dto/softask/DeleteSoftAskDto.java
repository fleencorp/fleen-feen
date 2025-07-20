package com.fleencorp.feen.softask.model.dto.softask;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteSoftAskDto {

  private Long softAskId;

  public static DeleteSoftAskDto of(final Long softAskId) {
    return new DeleteSoftAskDto(softAskId);
  }
}
