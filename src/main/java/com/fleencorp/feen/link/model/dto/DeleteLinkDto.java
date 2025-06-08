package com.fleencorp.feen.link.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.dto.base.BaseLinkDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeleteLinkDto extends BaseLinkDto {

  @Valid
  @NotNull(message = "{link.links.NotEmpty}")
  @Size(max = 12, message = "{link.links.Size}")
  @JsonProperty("links")
  private List<LinkDto> links = new ArrayList<>();

  public List<LinkType> getLinkTypes() {
    return links.stream()
      .filter(Objects::nonNull)
      .map(LinkDto::getLinkType)
      .filter(Objects::nonNull)
      .toList();
  }


  @Getter
  @Setter
  public static class LinkDto {

    @NotNull(message = "{link.linkType.NotNull}")
    @OneOf(enumClass = LinkType.class, message = "{link.linkType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("link_type")
    private String linkType;

    public LinkType getLinkType() {
      return LinkType.of(linkType);
    }
  }
}
