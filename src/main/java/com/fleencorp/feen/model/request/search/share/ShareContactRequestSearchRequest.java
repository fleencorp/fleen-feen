package com.fleencorp.feen.model.request.search.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.share.ShareContactRequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ShareContactRequestSearchRequest extends SearchRequest {

  @JsonProperty("share_contact_request_status")
  private String shareContactRequestStatus;

  @JsonProperty("is_expected")
  private Boolean isExpected;

  public ShareContactRequestStatus getActualShareContactRequestStatus(final ShareContactRequestStatus defaultShareContactRequestStatus) {
    final ShareContactRequestStatus actualShareContactRequestStatus1 = parseEnumOrNull(shareContactRequestStatus, ShareContactRequestStatus.class);
    if (nonNull(actualShareContactRequestStatus1)) {
      return actualShareContactRequestStatus1;
    }
    return defaultShareContactRequestStatus;
  }

}
