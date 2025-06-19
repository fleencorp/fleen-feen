package com.fleencorp.feen.user.model.response.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.user.model.response.member.MemberUpdateInfoResponse;
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
public class RetrieveMemberUpdateInfoResponse extends LocalizedResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.member.update.info";
  }

  @JsonProperty("details")
  private MemberUpdateInfoResponse details;

  public static RetrieveMemberUpdateInfoResponse of(final MemberUpdateInfoResponse details) {
    return new RetrieveMemberUpdateInfoResponse(details);
  }
}
