package com.fleencorp.feen.link.mapper.impl;

import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.mapper.LinkMapper;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.info.LinkTypeInfo;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class LinkMapperImpl implements LinkMapper {

  /**
   * Converts a {@link Link} entity into a {@link LinkResponse}.
   *
   * <p>If the provided link is not {@code null}, a new {@link LinkResponse} is created with
   * its type information mapped into a {@link LinkTypeInfo}, and its URL preserved.
   * If the provided link is {@code null}, this method returns {@code null}.</p>
   *
   * @param entry the {@link Link} entity to convert
   * @return a {@link LinkResponse} containing the mapped link type and URL,
   *         or {@code null} if the given entry is {@code null}
   */
  private LinkResponse toLinkResponse(final Link entry) {
    if (nonNull(entry)) {
      final LinkType linkType = entry.getLinkType();
      final LinkTypeInfo linkTypeInfo = LinkTypeInfo.of(
        linkType,
        linkType.getValue(),
        linkType.getBusinessFormat(),
        linkType.getCommunityFormat()
      );

      final LinkResponse linkResponse = new LinkResponse();
      linkResponse.setLinkType(linkTypeInfo);
      linkResponse.setUrl(entry.getUrl());

      return linkResponse;
    }

    return null;
  }

  /**
   * Converts a list of {@code Link} objects to a list of {@code LinkResponse} objects.
   *
   * <p>This method processes a list of {@code Link} entries, filtering out any null entries, and converts each
   * non-null entry into a {@code LinkResponse} using the {@code toLinkResponse} method. If the input list is null or empty,
   * it returns an empty list.
   *
   * @param entries the list of {@code Link} objects to be converted
   * @return a list of {@code LinkResponse} objects corresponding to the provided {@code Link} entries, or an empty list if the input is null or empty
   */
  @Override
  public Collection<LinkResponse> toLinkResponses(final Collection<Link> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toLinkResponse)
        .toList();
    }

    return List.of();
  }
}
