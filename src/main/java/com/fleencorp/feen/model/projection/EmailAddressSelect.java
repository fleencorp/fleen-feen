package com.fleencorp.feen.model.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailAddressSelect {

  private Long memberId;
  private String emailAddress;
}