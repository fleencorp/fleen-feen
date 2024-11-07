package com.fleencorp.feen.model.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "member_id",
  "first_name",
  "last_name",
  "email_address",
  "phone_number",
  "country"
})
public class MemberUpdateSelect {

  @JsonProperty("member_id")
  protected Long memberId;

  @JsonProperty("first_name")
  protected String firstName;

  @JsonProperty("last_name")
  protected String lastName;

  @JsonProperty("email_address")
  protected String emailAddress;

  @JsonProperty("phone_number")
  protected String phoneNumber;

  @JsonProperty("country")
  protected String country;
}
