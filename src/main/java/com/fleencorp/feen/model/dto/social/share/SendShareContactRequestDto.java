package com.fleencorp.feen.model.dto.social.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendShareContactRequestDto {

  @NotNull(message = "{share.recipient.NotNull}")
  @IsNumber
  @JsonProperty("recipient_id")
  private String recipientId;

  @NotNull(message = "{share.contactType.NotNull}")
  @ValidEnum(enumClass = ContactType.class, message = "{share.contactType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("contact_type")
  private String contactType;

  @Size(min = 10, max = 500, message = "{share.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public ShareContactRequest toShareContactRequest(final Long initiatorId) {
    final ShareContactRequest shareContactRequest = toShareContactRequest();
    shareContactRequest.setInitiator(Member.of(initiatorId));
    return shareContactRequest;
  }

  public ShareContactRequest toShareContactRequest() {
    return ShareContactRequest.builder()
        .isExpected(false)
        .contactType(ContactType.of(contactType))
        .requestStatus(ShareContactRequestStatus.SENT)
        .recipient(Member.of(recipientId))
        .initiatorComment(comment)
        .build();
  }

  public Long getActualRecipientId() {
    return Long.parseLong(recipientId);
  }

  public Member getRecipient() {
    return Member.of(recipientId);
  }
}
