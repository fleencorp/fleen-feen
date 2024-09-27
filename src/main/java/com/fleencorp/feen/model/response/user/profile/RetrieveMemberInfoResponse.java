package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.projection.MemberInfoSelect;
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
  "details"
})
public class RetrieveMemberInfoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.member.info";
  }

  @JsonProperty("details")
  private MemberInfoSelect details;

  public static RetrieveMemberInfoResponse of(final MemberInfoSelect details) {
    return RetrieveMemberInfoResponse.builder()
      .details(details)
      .build();
  }
}
