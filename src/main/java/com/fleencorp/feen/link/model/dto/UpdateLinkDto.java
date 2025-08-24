package com.fleencorp.feen.link.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.dto.base.BaseLinkDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLinkDto extends BaseLinkDto {

  @Valid
  @NotEmpty(message = "{link.links.NotEmpty}")
  @Size(max = 12, message = "{link.links.Size}")
  @JsonProperty("links")
  private List<LinkDto> links = new ArrayList<>();

  /**
   * Returns a list of valid, unique links, with one link per type.
   *
   * <p>This method filters and returns a list of links, ensuring each link type appears only once.
   * It checks if the links list is null or empty and returns an empty list in that case.
   *
   * @return a list of {@code LinkDto} objects, each representing a valid, unique link type
   */
  public List<LinkDto> getLinks() {
    if (links == null || links.isEmpty()) {
      return Collections.emptyList();
    }

    // Create a map to store links uniquely by their type
    final Map<LinkType, LinkDto> uniqueByType = new LinkedHashMap<>();
    // Iterate through the links to add valid and non-null ones to the map
    for (final LinkDto dto : links) {
      if (nonNull(dto) && dto.isValid()) {
        // Only add the first occurrence of each link type
        uniqueByType.putIfAbsent(dto.getLinkType(), dto);
      }
    }

    return new ArrayList<>(uniqueByType.values());
  }

  /**
   * Retrieves the valid link types from the list of links.
   *
   * <p>This method filters out invalid links and returns a set of the link types
   * that are associated with valid links.</p>
   *
   * @return A {@link Set} of {@link LinkType} representing the valid link types.
   */
  public Set<LinkType> getValidLinkTypes() {
    return links.stream()
      .filter(LinkDto::isValid)
      .map(LinkDto::getLinkType)
      .collect(Collectors.toSet());
  }

  @Valid
  @Getter
  @Setter
  public static class LinkDto {

    @NotBlank(message = "{link.url.NotBlank}")
    @Size(min = 1, max = 1000, message = "{link.url.Size}")
    @JsonProperty("url")
    private String url;

    @NotNull(message = "{link.linkType.NotNull}")
    @OneOf(enumClass = LinkType.class, message = "{link.linkType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty("link_type")
    private String linkType;

    public LinkType getLinkType() {
      return LinkType.of(linkType);
    }

    public boolean isValid() {
      return url != null && !url.isBlank() && getLinkType() != null;
    }

    public boolean isInvalid() {
      return !isValid();
    }

  }
}

