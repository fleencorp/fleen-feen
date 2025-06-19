package com.fleencorp.feen.user.model.response.member;

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
public class MemberInfoResponse {

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

  public static MemberInfoResponse of(final Long memberId, final String firstName, final String lastName, final String profilePhoto, final String country) {
    return new MemberInfoResponse(memberId, firstName, lastName, profilePhoto, country);
  }
}
