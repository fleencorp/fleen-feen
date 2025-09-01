package com.fleencorp.feen.business.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToSentenceCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.business.constant.BusinessChannelType;
import com.fleencorp.feen.business.constant.BusinessStatus;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.common.validator.FoundingYear;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddBusinessDto {

  @NotBlank(message = "{business.title.NotBlank}")
  @Size(min = 10, max = 300, message = "{business.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  protected String title;

  @NotBlank(message = "{business.motto.NotBlank}")
  @Size(min = 10, max = 500, message = "{business.motto.Size}")
  @ToTitleCase
  @JsonProperty("motto")
  protected String motto;

  @NotBlank(message = "{business.description.NotBlank}")
  @Size(max = 3000, message = "{business.description.Size}")
  @ToSentenceCase
  @JsonProperty("description")
  protected String description;

  @NotBlank(message = "{business.address.NotBlank}")
  @Size(max = 500, message = "{business.address.Size}")
  @ToTitleCase
  @JsonProperty("address")
  protected String address;

  @NotBlank(message = "{business.country.NotBlank}")
  @Size(max = 200, message = "{business.country.Size}")
  @JsonProperty("country")
  protected String country;

  @NotBlank(message = "{business.otherDetails.NotBlank}")
  @Size(max = 3000, message = "{business.otherDetails.Size}")
  @ToSentenceCase
  @JsonProperty("other_details")
  protected String otherDetails;

  @NotBlank(message = "{business.foundingYear.NotBlank}")
  @IsNumber(message = "{business.foundingYear.IsNumber}")
  @FoundingYear(message = "{business.foundingYear.Pattern}")
  @JsonProperty("foundingYear")
  protected String foundingYear;

  @NotNull(message = "{business.channel.NotNull}")
  @OneOf(enumClass = BusinessChannelType.class, message = "{business.channel.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("channel")
  protected String channel;

  @NotNull(message = "{business.status.NotNull}")
  @OneOf(enumClass = BusinessStatus.class, message = "{business.status.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("status")
  protected String status;

  public BusinessChannelType getBusinessChannelType() {
    return BusinessChannelType.of(channel);
  }

  public BusinessStatus getBusinessStatus() {
    return BusinessStatus.of(status);
  }

  public Integer getFoundingYear() {
    return nonNull(foundingYear) ? Integer.parseInt(foundingYear.trim()) : null;
  }

  public Business toBusiness(final Member member) {
    final Business business = new Business();
    business.setTitle(title);
    business.setDescription(description);
    business.setOtherDetails(otherDetails);
    business.setMotto(motto);

    business.setChannelType(getBusinessChannelType());
    business.setStatus(getBusinessStatus());

    business.setFoundingYear(getFoundingYear());
    business.setAddress(address);
    business.setCountry(country);

    business.setOwnerId(member.getMemberId());

    return business;
  }
}
