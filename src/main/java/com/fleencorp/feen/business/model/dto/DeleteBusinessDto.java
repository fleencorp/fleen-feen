package com.fleencorp.feen.business.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteBusinessDto {

  private Long businessId;

  public static DeleteBusinessDto of(final Long businessId) {
    return new DeleteBusinessDto(businessId);
  }
}
