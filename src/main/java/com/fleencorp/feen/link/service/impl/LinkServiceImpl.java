package com.fleencorp.feen.link.service.impl;

import com.fleencorp.feen.business.exception.BusinessNotFoundException;
import com.fleencorp.feen.business.exception.BusinessNotOwnerException;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.service.BusinessOperationService;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.link.model.factory.LinkFactory;
import com.fleencorp.feen.link.model.holder.LinkParentDetailHolder;
import com.fleencorp.feen.link.model.response.LinkDeleteResponse;
import com.fleencorp.feen.link.model.response.LinkStreamMusicUpdateResponse;
import com.fleencorp.feen.link.model.response.LinkUpdateResponse;
import com.fleencorp.feen.link.repository.LinkRepository;
import com.fleencorp.feen.link.service.LinkService;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.stream.service.core.StreamService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.feen.link.model.dto.UpdateLinkDto.LinkDto;
import static java.util.Objects.nonNull;

@Service
public class LinkServiceImpl implements LinkService {

  private final BusinessOperationService businessOperationService;
  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final StreamOperationsService streamOperationsService;
  private final StreamService streamService;
  private final LinkRepository linkRepository;
  private final Localizer localizer;

  public LinkServiceImpl(
      final BusinessOperationService businessOperationService,
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final StreamOperationsService streamOperationsService,
      final StreamService streamService,
      final LinkRepository linkRepository,
      final Localizer localizer) {
    this.businessOperationService = businessOperationService;
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.streamService = streamService;
    this.streamOperationsService = streamOperationsService;
    this.linkRepository = linkRepository;
    this.localizer = localizer;
  }

  /**
   * Updates the music link for a specific stream.
   *
   * <p>This method updates the music link associated with a stream. It first retrieves the stream using the provided
   * stream ID, checks if the user is the organizer of the stream, validates the link, and then updates the music link
   * for the stream. Finally, it saves the updated stream and returns a localized response indicating the update was successful.
   *
   * @param updateStreamMusicLinkDto the data transfer object containing the new music link and stream ID
   * @param user the user requesting the update, used to verify if they are the organizer
   * @return a localized {@code UpdateStreamMusicLinkResponse} indicating the result of the update operation
   */
  @Override
  @Transactional
  public LinkStreamMusicUpdateResponse updateStreamMusicLink(final UpdateStreamMusicLinkDto updateStreamMusicLinkDto, final RegisteredUser user) {
    // Retrieve the stream object based on the stream ID
    final FleenStream stream = streamService.findStream(updateStreamMusicLinkDto.getStreamId());
    // Ensure the user is the organizer of the stream
    stream.checkIsOrganizer(user.getId());
    // Validate the provided music link
    updateStreamMusicLinkDto.checkLinkIsValid();
    // Update the stream's music link
    stream.setMusicLink(updateStreamMusicLinkDto.getMusicLink());
    // Save the updated stream to the repository
    streamOperationsService.save(stream);
    // Return a localized response indicating the update was successful
    return localizer.of(LinkStreamMusicUpdateResponse.of());
  }

  @Override
  @Transactional
  public LinkUpdateResponse updateChatSpaceLink(final UpdateLinkDto updateLinkDto, final RegisteredUser user) throws ChatSpaceNotFoundException, FailedOperationException {
    final Long parentId = updateLinkDto.getParentId();
    final LinkParentType linkParentType = updateLinkDto.getParentLinkType();
    final Member member = user.toMember();

    final LinkParentDetailHolder linkParentDetailHolder = findOrVerifyLinkParent(parentId, linkParentType, member);
    final List<Link> existingLinks = findParentExistingLinks(parentId, linkParentType);

    updateLinks(updateLinkDto, existingLinks, linkParentDetailHolder, member);
    // Return a localized response indicating the update was successful
    return localizer.of(LinkUpdateResponse.of());
  }

  private void updateLinks(final UpdateLinkDto updateLinkDto, final Collection<Link> existingLinks, final LinkParentDetailHolder linkParentDetailHolder, final Member member) {
    // Create a map of existing links by link type for easier lookup
    final Map<LinkType, Link> existingLinksMap = existingLinks.stream()
      .collect(Collectors.toMap(Link::getLinkType, link -> link));
    // Create a set of incoming link types for comparison
    final Set<LinkType> incomingLinkTypes = updateLinkDto.getValidLinkTypes();
    // Process each link from the update request
    upsertLinks(updateLinkDto.getLinks(), existingLinksMap, linkParentDetailHolder, member);
    // Remove stale or old links
    removeStaleLinks(existingLinks, incomingLinkTypes);
  }

  /**
   * Removes links from the repository that are no longer present in the incoming link types.
   *
   * <p>This method iterates over the existing links and deletes those whose link type
   * is not found in the incoming set of valid link types. This helps clean up any stale links.</p>
   *
   * @param existingLinks A list of the existing links to be checked.
   * @param incomingLinkTypes A set of valid link types that should remain.
   */
  protected void removeStaleLinks(final Collection<Link> existingLinks, final Collection<LinkType> incomingLinkTypes) {
    if (nonNull(existingLinks) && nonNull(incomingLinkTypes)) {
      // Remove links that are no longer present in the incoming update
      for (final Link existingLink : existingLinks) {
        if (!incomingLinkTypes.contains(existingLink.getLinkType())) {
          linkRepository.delete(existingLink);
        }
      }
    }
  }

  protected void upsertLinks(final List<LinkDto> linksDto, final Map<LinkType, Link> existingLinksMap, final LinkParentDetailHolder linkParentDetailHolder, final Member member) {
    // Process each link from the update request
    for (final LinkDto dto : linksDto) {
      // Skip invalid link DTOs
      if (dto.isInvalid()) {
        continue;
      }

      final LinkType linkType = dto.getLinkType();
      final String url = dto.getUrl();

      // If the link type already exists, update the URL
      if (existingLinksMap.containsKey(linkType)) {
        final Link existingLink = existingLinksMap.get(linkType);
        existingLink.setUrl(url);
      } else {
        // If the link type does not exist, create and save a new link
        final Link newLink = LinkFactory.by(dto, linkParentDetailHolder, member);
        linkRepository.save(newLink);
      }
    }
  }

  @Override
  @Transactional
  public LinkDeleteResponse deleteLinks(final DeleteLinkDto deleteLinkDto, final RegisteredUser user)
      throws BusinessNotFoundException, BusinessNotOwnerException, ChatSpaceNotFoundException,
        ChatSpaceNotAnAdminException, FailedOperationException {
    final Long parentId = deleteLinkDto.getParentId();
    final List<LinkType> linkTypes = deleteLinkDto.getLinkTypes();
    final LinkParentType linkParentType = deleteLinkDto.getParentLinkType();
    final Member member = user.toMember();

    findOrVerifyLinkParent(parentId, linkParentType, member);
    final Collection<Link> links = findParentExistingLinks(parentId, linkParentType, linkTypes);

    linkRepository.deleteAll(links);
    final LinkDeleteResponse linkDeleteResponse = LinkDeleteResponse.of();
    return localizer.of(linkDeleteResponse);
  }

  private LinkParentDetailHolder findOrVerifyLinkParent(final Long parentId, final LinkParentType linkParentType, final Member member) {
    checkIsNullAny(List.of(parentId, linkParentType), FailedOperationException::new);

    switch (linkParentType) {
      case STREAM, USER: throw FailedOperationException.of();
    }

    final Business business = LinkParentType.isBusiness(linkParentType) ? businessOperationService.findBusinessAndVerifyOwner(parentId, member) : null;
    final ChatSpace chatSpace = LinkParentType.isChatSpace(linkParentType) ? chatSpaceOperationsService.findChatSpaceAndVerifyAdmin(parentId, member) : null;

    return LinkParentDetailHolder.of(business, chatSpace, linkParentType);
  }

  private List<Link> findParentExistingLinks(final Long parentId, final LinkParentType linkParentType) {
    checkIsNullAny(List.of(parentId, linkParentType), FailedOperationException::new);

    return switch (linkParentType) {
      case BUSINESS -> linkRepository.findByBusiness(parentId, LinkParentType.BUSINESS);
      case CHAT_SPACE -> linkRepository.findByChatSpace(parentId, LinkParentType.CHAT_SPACE);
      case STREAM, USER -> throw FailedOperationException.of();
    };
  }

  /**
   * Retrieves existing {@link Link} entries for the given parent entity and link types.
   *
   * <p>This method validates that the provided parameters are not null and then queries
   * the repository based on the specified {@link LinkParentType}. Supported parent types
   * are {@link LinkParentType#BUSINESS} and {@link LinkParentType#CHAT_SPACE}.
   * For unsupported types such as {@link LinkParentType#STREAM} and {@link LinkParentType#USER},
   * a {@link FailedOperationException} is thrown.</p>
   *
   * @param parentId the identifier of the parent entity
   * @param linkParentType the type of the parent entity (e.g., business, chat space)
   * @param linkTypes the list of link types to filter the search
   * @return the list of matching {@link Link} entries
   * @throws FailedOperationException if any parameter is null or if the parent type is unsupported
   */
  private List<Link> findParentExistingLinks(final Long parentId, final LinkParentType linkParentType, final List<LinkType> linkTypes) throws FailedOperationException {
    checkIsNullAny(List.of(parentId, linkParentType, linkTypes), FailedOperationException::new);

    return switch (linkParentType) {
      case BUSINESS -> linkRepository.findByBusiness(parentId, LinkParentType.BUSINESS, linkTypes);
      case CHAT_SPACE -> linkRepository.findByChatSpace(parentId, LinkParentType.CHAT_SPACE, linkTypes);
      case STREAM, USER -> throw FailedOperationException.of();
    };
  }

}
