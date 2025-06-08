package com.fleencorp.feen.adapter.google.oauth2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Oauth2UserResponse {

  @JsonProperty("family_name")
  private String familyName;

  @JsonProperty("name")
  private String name;

  @JsonProperty("email")
  private String emailAddress;

  @JsonProperty("picture")
  private String pictureUrl;

  @JsonProperty("given_name")
  private String givenName;

  @JsonProperty("verified_email")
  private String verifiedEmail;

  public String getFirstName() {
    return name;
  }

  public String getLastName() {
    return familyName;
  }
}
