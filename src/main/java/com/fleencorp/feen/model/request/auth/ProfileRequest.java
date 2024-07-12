package com.fleencorp.feen.model.request.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {

  protected String firstName;
  protected String lastName;
  protected String emailAddress;
  protected String phoneNumber;
}
