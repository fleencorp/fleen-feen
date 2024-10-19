package com.fleencorp.feen.model.response.social.share;

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
  "share_contact_request_id",
  "full_name",
  "user_id",
  "request_status"
})
public class ShareContactRequestResponse {

  @JsonProperty("share_contact_request_id")
  private Long shareContactRequestId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonFormat(shape = STRING)
  @JsonProperty("request_status")
  private ShareContactRequestStatus requestStatus;

  @JsonProperty("user_id")
  private Long userId;

  public static ShareContactRequestResponse of(final Long shareContactRequestId, final ShareContactRequestStatus shareContactRequestStatus, final String fullName, final Long userId) {
    return ShareContactRequestResponse.builder()
        .shareContactRequestId(shareContactRequestId)
        .requestStatus(shareContactRequestStatus)
        .fullName(fullName)
        .userId(userId)
        .build();
  }
}
