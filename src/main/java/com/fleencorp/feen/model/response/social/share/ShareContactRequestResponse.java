package com.fleencorp.feen.model.response.social.share;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "share_contact_request_id",
  "full_name",
  "user_id",
  "share_contact_request_status_info"
})
public class ShareContactRequestResponse {

  @JsonProperty("share_contact_request_id")
  private Long shareContactRequestId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("share_contact_request_status_info")
  private ShareContactRequestStatusInfo requestStatusInfo;

  public static ShareContactRequestResponse of(final Long shareContactRequestId, final String fullName, final Long userId, final ShareContactRequestStatusInfo requestStatusInfo) {
    return ShareContactRequestResponse.builder()
        .shareContactRequestId(shareContactRequestId)
        .requestStatusInfo(requestStatusInfo)
        .fullName(fullName)
        .userId(userId)
        .build();
  }
}
