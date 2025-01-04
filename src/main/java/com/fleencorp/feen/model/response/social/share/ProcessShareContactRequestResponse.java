package com.fleencorp.feen.model.response.social.share;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
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
  "message",
  "share_contact_request_status_info"
})
public class ProcessShareContactRequestResponse extends ApiResponse {

  @JsonProperty("share_contact_request_status_info")
  private ShareContactRequestStatusInfo requestStatusInfo;

  @Override
  public String getMessageCode() {
    return "process.share.contact.request";
  }

  public static ProcessShareContactRequestResponse of(final ShareContactRequestStatusInfo requestStatusInfo) {
    return new ProcessShareContactRequestResponse(requestStatusInfo);
  }
}
