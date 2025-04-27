package com.fleencorp.feen.model.info.stream;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "other_details",
  "other_link",
  "group_or_organization_name"
})
public class OtherStreamDetailInfo {

  @JsonProperty("other_details")
  private String otherDetails;

  @JsonProperty("other_link")
  private String otherLink;

  @JsonProperty("group_or_organization_name")
  private String groupOrOrganizationName;

  public static OtherStreamDetailInfo of(final String otherDetails, final String otherLink, final String groupOrOrganizationName) {
    return new OtherStreamDetailInfo(otherDetails, otherLink, groupOrOrganizationName);
  }
}
