package com.fleencorp.feen.model.info.share.contact.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "request_status",
  "request_status_text",
  "request_status_text_2",
})
public class ShareContactRequestStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("request_status")
  private ShareContactRequestStatus requestStatus;

  @JsonProperty("request_status_text")
  private String requestStatusText;

  @JsonProperty("request_status_text_2")
  private String requestStatusText2;

  public static ShareContactRequestStatusInfo of(final ShareContactRequestStatus requestStatus, final String requestStatusText, final String requestStatusText2) {
    return new ShareContactRequestStatusInfo(requestStatus, requestStatusText, requestStatusText2);
  }
}
