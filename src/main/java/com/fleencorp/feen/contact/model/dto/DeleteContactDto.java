package com.fleencorp.feen.contact.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteContactDto {

  private Long contactId;

  public static DeleteContactDto of(final Long contactId) {
    return new DeleteContactDto(contactId);
  }
}
