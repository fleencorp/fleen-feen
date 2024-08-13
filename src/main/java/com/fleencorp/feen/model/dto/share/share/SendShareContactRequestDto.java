package com.fleencorp.feen.model.dto.share.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.share.ContactType;
import com.fleencorp.feen.constant.share.ShareContactRequestStatus;
import com.fleencorp.feen.converter.common.ToUpperCase;
import com.fleencorp.feen.model.domain.share.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
  @Min(value = 0, message = "{share.recipient.Min}")
  @Max(value = Long.MAX_VALUE, message = "{share.recipient.Max}")
  @JsonProperty("recipient_id")
  private Long recipientId;

  @NotNull(message = "{share.contactType.NotNull}")
  @ValidEnum(enumClass = ContactType.class, message = "{share.contactType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("contact_type")
  private String contactType;

  @Size(min = 10, max = 500, message = "{share.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public ShareContactRequest toShareContactRequest(final Long initiatorId) {
    return ShareContactRequest.builder()
        .isExpected(false)
        .contactType(ContactType.of(contactType))
        .shareContactRequestStatus(ShareContactRequestStatus.SENT)
        .initiator(Member.of(initiatorId))
        .recipient(Member.of(recipientId))
        .initiatorComment(comment)
        .build();
  }
}
