package com.fleencorp.feen.model.projection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "member_id",
  "first_name",
  "last_name",
  "profile_photo",
  "country"
})
public class MemberInfoSelect {

  @JsonProperty("member_id")
  protected Long memberId;

  @JsonProperty("first_name")
  protected String firstName;

  @JsonProperty("last_name")
  protected String lastName;

  @JsonProperty("profile_photo")
  protected String profilePhoto;

  @JsonProperty("country")
  protected String country;
}
