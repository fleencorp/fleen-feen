package com.fleencorp.feen.softask.model.response.softask;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
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
  "message",
  "soft_ask_id",
  "soft_ask"
})
public class SoftAskAddResponse extends LocalizedResponse {

  @JsonProperty("soft_ask_id")
  private Long softAskId;

  @JsonProperty("soft_ask")
  private SoftAskResponse softAskResponse;

  @Override
  public String getMessageCode() {
    return "soft.ask.add";
  }

  public static SoftAskAddResponse of(final Long softAskId, final SoftAskResponse softAskResponse) {
    return new SoftAskAddResponse(softAskId, softAskResponse);
  }
}
