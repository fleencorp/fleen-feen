package com.fleencorp.feen.business.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.business.model.response.core.BusinessResponse;
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
  "business"
})
public class BusinessAddResponse extends LocalizedResponse {

  @JsonProperty("business_id")
  private Long businessId;

  @JsonProperty("business")
  private BusinessResponse business;

  @Override
  public String getMessageCode() {
    return "business.add";
  }

  public static BusinessAddResponse of(final Long businessId, final BusinessResponse business) {
    return new BusinessAddResponse(businessId, business);
  }
}
