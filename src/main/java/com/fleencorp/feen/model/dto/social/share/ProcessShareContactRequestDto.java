package com.fleencorp.feen.model.dto.social.share;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.constant.social.ContactType;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessShareContactRequestDto {

  @NotNull(message = "{share.shareContactRequestStatus.NotNull}")
  @OneOf(enumClass = ShareContactRequestStatus.class, message = "{share.shareContactRequestStatus.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("share_contact_request_status")
  private String shareContactRequestStatus;

  @NotNull(message = "{share.contactType.NotNull}")
  @OneOf(enumClass = ContactType.class, message = "{share.contactType.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("contact_type")
  private String contactType;

  @NotBlank(message = "{share.contact.NotBlank}")
  @Size(min = 1, max = 1000, message = "{share.contact.Size}")
  @JsonProperty("contact")
  private String contact;

  @Size(min = 10, max = 500, message = "{share.comment.Size}")
  @JsonProperty("comment")
  protected String comment;

  public ShareContactRequestStatus getActualShareContactRequestStatus() {
    return ShareContactRequestStatus.of(shareContactRequestStatus);
  }

  public ContactType getActualContactType() {
    return ContactType.of(contactType);
  }
}
