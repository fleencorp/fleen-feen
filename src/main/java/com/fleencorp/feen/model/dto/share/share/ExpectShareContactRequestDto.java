package com.fleencorp.feen.model.dto.share.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.model.domain.share.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpectShareContactRequestDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @Min(value = 0, message = "{share.recipient.Min}")
  @Max(value = Long.MAX_VALUE, message = "{share.recipient.Max}")
  @JsonProperty("recipient_id")
  private Long recipientId;

  public ShareContactRequest toShareContactRequest(final Long initiatorId) {
    final ShareContactRequest shareContactRequest = toShareContactRequest();
    shareContactRequest.setInitiator(Member.of(initiatorId));
    return shareContactRequest;
  }

  public ShareContactRequest toShareContactRequest() {
    return ShareContactRequest.builder()
        .isExpected(true)
        .contactType(null)
        .shareContactRequestStatus(null)
        .recipient(Member.of(recipientId))
        .build();
  }
}
