package com.fleencorp.feen.business.model.response.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.business.model.info.BusinessChannelTypeInfo;
import com.fleencorp.feen.business.model.info.BusinessStatusInfo;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.common.model.response.core.FleenFeenResponse;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.model.contract.HasLinks;
import com.fleencorp.feen.model.contract.HasSlug;
import com.fleencorp.feen.model.contract.Updatable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "id",
  "title",
  "motto",
  "description",
  "other_details",
  "founding_year",
  "address",
  "country",
  "channel_type_info",
  "status_info",
  "links",
  "share_count_info",
  "slug",
  "is_updatable",
  "created_on",
  "updated_on"
})
public class BusinessResponse extends FleenFeenResponse
  implements HasLinks, HasSlug, Updatable {

  @JsonProperty("title")
  private String title;

  @JsonProperty("motto")
  private String motto;

  @JsonProperty("description")
  private String description;

  @JsonProperty("other_details")
  private String otherDetails;

  @JsonProperty("founding_year")
  private Integer foundingYear;

  @JsonProperty("address")
  private String address;

  @JsonProperty("country")
  private String country;

  @JsonProperty("channel_type_info")
  private BusinessChannelTypeInfo channelTypeInfo;

  @JsonProperty("status_info")
  private BusinessStatusInfo statusInfo;

  @JsonProperty("links")
  private Collection<LinkResponse> links = new HashSet<>();

  @JsonProperty("share_count_info")
  private ShareCountInfo shareCountInfo;

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("is_updatable")
  private Boolean isUpdatable;

  @JsonIgnore
  private Long authorId;

  @JsonIgnore
  private Long organizerId;

  @Override
  public void setIsUpdatable(final boolean isUpdatable) {
    this.isUpdatable = isUpdatable;
  }

  @Override
  public void markAsUpdatable() {
    setIsUpdatable(true);
  }
}
