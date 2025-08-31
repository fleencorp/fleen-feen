package com.fleencorp.feen.model.request.search.social;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.common.constant.social.ShareContactRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShareContactRequestSearchRequest extends SearchRequest {

  @JsonProperty("share_contact_request_status")
  private String shareContactRequestStatus;

  @JsonProperty("is_expected")
  private Boolean isExpected;

  @JsonIgnore
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private Boolean isSentExpectedRequest = false;

  @JsonIgnore
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Boolean isReceivedExpectedRequest = false;

  @JsonIgnore
  public void setSentExpectedRequest() {
    this.isSentExpectedRequest = true;
  }

  @JsonIgnore
  public void setReceivedExpectedRequest() {
    this.isReceivedExpectedRequest = true;
  }

  public ShareContactRequestStatus getShareContactRequestStatus(final ShareContactRequestStatus defaultShareContactRequestStatus) {
    final ShareContactRequestStatus actualShareContactRequestStatus = ShareContactRequestStatus.of(shareContactRequestStatus);
    return nonNull(actualShareContactRequestStatus) ? actualShareContactRequestStatus : defaultShareContactRequestStatus;
  }


}
