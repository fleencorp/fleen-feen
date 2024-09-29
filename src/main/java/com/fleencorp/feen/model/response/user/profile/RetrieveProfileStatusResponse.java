package com.fleencorp.feen.model.response.user.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import com.fleencorp.feen.model.projection.MemberProfileStatusSelect;
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
public class RetrieveProfileStatusResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "retrieve.profile.status";
  }

  @JsonProperty("details")
  private MemberProfileStatusSelect details;

  public static RetrieveProfileStatusResponse of(final MemberProfileStatusSelect details) {
    return RetrieveProfileStatusResponse.builder()
      .details(details)
      .build();
  }
}
