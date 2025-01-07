package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.projection.MemberInfoSelect;
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
public class RetrieveMemberInfoResponse extends ApiResponse {

  @JsonProperty("details")
  private MemberInfoSelect details;

  @Override
  public String getMessageCode() {
    return "retrieve.member.info";
  }

  public static RetrieveMemberInfoResponse of(final MemberInfoSelect details) {
    return new RetrieveMemberInfoResponse(details);
  }
}
