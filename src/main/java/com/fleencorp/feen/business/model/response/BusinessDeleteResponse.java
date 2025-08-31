package com.fleencorp.feen.business.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "business_id",
})
public class BusinessDeleteResponse extends LocalizedResponse {

  @JsonProperty("business_id")
  private Long businessId;

  @Override
  public String getMessageCode() {
    return "business.delete";
  }

  public static BusinessDeleteResponse of(final Long businessId) {
    return new BusinessDeleteResponse(businessId);
  }
}
