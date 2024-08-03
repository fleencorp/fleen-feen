package com.fleencorp.feen.model.response.share.share;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
  "user_id"
})
public class ShareContactRequestResponse {

  @JsonProperty("share_contact_request_id")
  private Long shareContactRequestId;

  @JsonProperty("full_name")
  private String fullName;

  @JsonProperty("user_id")
  private Long userId;

  public static ShareContactRequestResponse of(final Long shareContactRequestId, final String fullName, final Long userId) {
    return ShareContactRequestResponse.builder()
        .shareContactRequestId(shareContactRequestId)
        .fullName(fullName)
        .userId(userId)
        .build();
  }
}
