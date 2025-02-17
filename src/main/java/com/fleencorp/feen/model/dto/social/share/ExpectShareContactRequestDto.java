package com.fleencorp.feen.model.dto.social.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpectShareContactRequestDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @IsNumber
  @JsonProperty("recipient_id")
  private String recipientId;

  public ShareContactRequest toShareContactRequest(final Member member) {
    final ShareContactRequest shareContactRequest = toShareContactRequest();
    shareContactRequest.setInitiator(member);
    return shareContactRequest;
  }

  public ShareContactRequest toShareContactRequest() {
    final ShareContactRequest shareContactRequest = new ShareContactRequest();
    shareContactRequest.setIsExpected(true);
    shareContactRequest.setContactType(null);
    shareContactRequest.setRequestStatus(null);
    shareContactRequest.setRecipient(Member.of(recipientId));

    return shareContactRequest;
  }

  public Long getRecipientId() {
    return Long.parseLong(recipientId);
  }
}
