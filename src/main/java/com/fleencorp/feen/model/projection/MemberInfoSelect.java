package com.fleencorp.feen.model.projection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberInfoSelect {

  @JsonProperty("member_id")
  protected Long memberId;

  @JsonProperty("first_name")
  protected String firstName;

  @JsonProperty("last_name")
  protected String lastName;

  @JsonProperty("country")
  protected String country;
}
