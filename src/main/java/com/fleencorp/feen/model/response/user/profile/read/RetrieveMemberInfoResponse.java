package com.fleencorp.feen.model.response.user.profile.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.member.MemberInfoResponse;
import com.fleencorp.localizer.model.response.LocalizedResponse;
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
  "details"
})
public class RetrieveMemberInfoResponse extends LocalizedResponse {

  @JsonProperty("details")
  private MemberInfoResponse details;

  @Override
  public String getMessageCode() {
    return "retrieve.member.info";
  }

  public static RetrieveMemberInfoResponse of(final MemberInfoResponse details) {
    return new RetrieveMemberInfoResponse(details);
  }
}
