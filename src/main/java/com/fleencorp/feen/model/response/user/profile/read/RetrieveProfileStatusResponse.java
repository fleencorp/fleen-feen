package com.fleencorp.feen.model.response.user.profile.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.response.member.MemberProfileStatusResponse;
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
public class RetrieveProfileStatusResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.profile.status";
  }

  @JsonProperty("details")
  private MemberProfileStatusResponse details;

  public static RetrieveProfileStatusResponse of(final MemberProfileStatusResponse details) {
    return new RetrieveProfileStatusResponse(details);
  }
}
