package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.projection.member.MemberUpdateSelect;
import com.fleencorp.localizer.model.response.ApiResponse;
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
public class RetrieveMemberUpdateInfoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.member.update.info";
  }

  @JsonProperty("details")
  private MemberUpdateSelect details;

  public static RetrieveMemberUpdateInfoResponse of(final MemberUpdateSelect details) {
    return new RetrieveMemberUpdateInfoResponse(details);
  }
}
