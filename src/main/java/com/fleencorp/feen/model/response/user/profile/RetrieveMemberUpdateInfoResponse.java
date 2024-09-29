package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.projection.MemberUpdateSelect;
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
public class RetrieveMemberUpdateInfoResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.member.update.info";
  }

  @JsonProperty("details")
  private MemberUpdateSelect details;

  public static RetrieveMemberUpdateInfoResponse of(final MemberUpdateSelect details) {
    return RetrieveMemberUpdateInfoResponse.builder()
      .details(details)
      .build();
  }
}
