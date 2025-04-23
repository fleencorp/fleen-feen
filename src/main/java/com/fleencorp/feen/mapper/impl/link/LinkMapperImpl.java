package com.fleencorp.feen.mapper.impl.link;

import com.fleencorp.feen.mapper.link.LinkMapper;
import com.fleencorp.feen.model.domain.other.Link;
import com.fleencorp.feen.model.info.link.LinkTypeInfo;
import com.fleencorp.feen.model.response.link.LinkResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Component
public class LinkMapperImpl implements LinkMapper {

  /**
   * Converts a {@code Link} object to a {@code LinkResponse}.
   *
   * <p>This method creates a {@code LinkResponse} from the given {@code Link} entry. If the {@code Link} entry is non-null,
   * it creates a {@code LinkTypeResponse} based on the link type information and sets the URL in the resulting
   * {@code LinkResponse}. If the {@code Link} entry is null, the method returns null.
   *
   * @param entry the {@code Link} object to be converted
   * @return a {@code LinkResponse} representing the {@code Link} object, or null if the entry is null
   */
  private LinkResponse toLinkResponse(final Link entry) {
    if (nonNull(entry)) {
      final LinkTypeInfo linkTypeInfo = LinkTypeInfo.of(
        entry.getLinkType(),
        entry.getLinkType().getValue(),
        entry.getLinkType().getFormat()
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
  public List<LinkResponse> toLinkResponses(final List<Link> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toLinkResponse)
        .toList();
    }

    return List.of();
  }
}
