package com.fleencorp.feen.link.service.impl;

import com.fleencorp.feen.constant.common.MusicLinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.mapper.LinkMapper;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.dto.DeleteLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateLinkDto;
import com.fleencorp.feen.link.model.dto.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.link.model.info.LinkTypeInfo;
import com.fleencorp.feen.link.model.info.MusicLinkTypeInfo;
import com.fleencorp.feen.link.model.response.LinkDeleteResponse;
import com.fleencorp.feen.link.model.response.LinkUpdateResponse;
import com.fleencorp.feen.link.model.response.LinkStreamMusicUpdateResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.link.repository.LinkRepository;
import com.fleencorp.feen.link.service.LinkService;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.user.security.RegisteredUser;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.link.model.dto.UpdateLinkDto.LinkDto;
import static java.util.Objects.nonNull;

@Service
public class LinkServiceImpl implements LinkService {

  private final ChatSpaceService chatSpaceService;
  private final StreamOperationsService streamOperationsService;
  private final StreamService streamService;
  private final LinkRepository linkRepository;
  private final LinkMapper linkMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code LinkServiceImpl}, which manages the lifecycle and retrieval of link entities
   * related to chat spaces and streams.
   *
   * @param chatSpaceService the service used to manage and validate chat space data
   * @param streamOperationsService the service for handling stream-level operations associated with links
   * @param streamService the higher-level service for managing streams
   * @param linkRepository the repository interface for performing persistence operations on link entities
   * @param linkMapper the mapper used to convert between link entities and their corresponding DTOs
   * @param localizer the utility component for resolving localized messages in responses
   */
  public LinkServiceImpl(
      final ChatSpaceService chatSpaceService,
      final StreamOperationsService streamOperationsService,
      final StreamService streamService,
      final LinkRepository linkRepository,
      final LinkMapper linkMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.streamService = streamService;
    this.streamOperationsService = streamOperationsService;
    this.linkRepository = linkRepository;
    this.linkMapper = linkMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves the available link types as a map of {@link LinkType} to their corresponding {@link LinkTypeInfo}.
   *
   * <p>This method iterates through all values of the {@link LinkType} enum and creates a {@link LinkTypeInfo}
   * for each, which contains the type, value, and format. The result is a map where the key is the {@link LinkType}
   * and the value is the corresponding {@link LinkTypeInfo}.</p>
   *
   * @return A {@link GetAvailableLinkTypeResponse} containing a map of available {@link LinkType}s and their
   *         associated {@link LinkTypeInfo}s.
   */
  @Override
  @Cacheable("availableLinkTypes")
  public GetAvailableLinkTypeResponse getAvailableLinkTypes() {
    final Map<LinkType, LinkTypeInfo> availableLinkTypes =
      Stream.of(LinkType.values())
        .collect(Collectors.collectingAndThen(
          Collectors.toMap(
            lt -> lt,
            lt -> LinkTypeInfo.of(lt, lt.getValue(), lt.getFormat()
            ),
            (_, b) -> b,
            () -> new EnumMap<>(LinkType.class)
          ),
          Map::copyOf
    ));

    // Create the response
    final GetAvailableLinkTypeResponse getAvailableLinkTypeResponse = GetAvailableLinkTypeResponse.of(availableLinkTypes);
    // Return the response
    return localizer.of(getAvailableLinkTypeResponse);
  }

  /**
   * Retrieves the available music link types as a map of {@link MusicLinkType} to their corresponding {@link MusicLinkTypeInfo}.
   *
   * <p>This method iterates through all values of the {@link MusicLinkType} enum and creates a {@link MusicLinkTypeInfo}
   * for each, which contains the type, value, and format. The result is a map where the key is the {@link MusicLinkType}
   * and the value is the corresponding {@link MusicLinkTypeInfo}.</p>
   *
   * @return A {@link GetAvailableMusicLinkTypeResponse} containing a map of available {@link MusicLinkType}s and their
   *         associated {@link MusicLinkTypeInfo}s.
   */
  @Override
  @Cacheable("availableMusicLinkTypes")
  public GetAvailableMusicLinkTypeResponse getAvailableMusicLinkType() {
    final Map<MusicLinkType, MusicLinkTypeInfo> availableLinkTypes =
      Stream.of(MusicLinkType.values())
        .collect(Collectors.collectingAndThen(
          Collectors.toMap(
            lt -> lt,
            lt -> MusicLinkTypeInfo.of(lt, lt.getValue(), lt.getFormat()
            ),
            (_, b) -> b,
            () -> new EnumMap<>(MusicLinkType.class)
          ),
          Map::copyOf
        ));

    // Create the response
    final GetAvailableMusicLinkTypeResponse getAvailableMusicLinkTypeResponse = GetAvailableMusicLinkTypeResponse.of(availableLinkTypes);
    // Return the response
    return localizer.of(getAvailableMusicLinkTypeResponse);
  }

  /**
   * Finds links based on the provided search request and user information.
   *
   * <p>This method processes the {@code searchRequest} to find links related to a specific chat space. It first updates the
   * page size to 1000, checks if the request is related to a chat space, retrieves the chat space details, and maps the
   * result into {@code LinkResponse} objects. Finally, it localizes the search result before returning it.
   *
   * @param searchRequest the request containing the search criteria for finding links
   * @param user the user requesting the search, used to determine updatable links
   * @return a {@code LinkSearchResult} containing the found links and other relevant search metadata
   */
  @Override
  public LinkSearchResult findLinks(final LinkSearchRequest searchRequest, final RegisteredUser user) {
    // Update the page size to 1000 for the search request
    searchRequest.updatePageSize(1000);
    // Initialize an empty page of links
    final Page<Link> page;
    // Initialize an empty link search result
    LinkSearchResult searchResult = LinkSearchResult.empty();

    // Check if the search request is related to a chat space and fetch the corresponding links
    if (searchRequest.isChatSpaceSearchRequest()) {
      page = linkRepository.findByChatSpaceId(searchRequest.getChatSpaceId(), searchRequest.getPage());
      // Retrieve the chat space details for the given chat space ID
      final ChatSpace chatSpace = chatSpaceService.findChatSpace(searchRequest.getChatSpaceId());
      // Map the links to their response objects
      final List<LinkResponse> views = linkMapper.toLinkResponses(page.getContent());
      // Set links that are updatable by the user
      setLinksThatAreUpdatableByUser(chatSpace, views, user);
      // Create the search result object with the views and pagination info
      searchResult = LinkSearchResult.of(toSearchResult(views, page), searchRequest.getParentId());
    }

    // Localize the search result before returning it
    return localizer.of(searchResult);
  }

  /**
   * Finds all links associated with a specific chat space.
   *
   * <p>This method retrieves all {@code Link} objects related to the given {@code chatSpaceId} and maps them to
   * {@code LinkResponse} objects using the {@code linkMapper}.
   *
   * @param chatSpaceId the ID of the chat space for which the links are being searched
   * @return a list of {@code LinkResponse} objects representing the links associated with the given chat space
   */
  @Override
  public List<LinkResponse> findChatSpaceLinks(final Long chatSpaceId) {
    // Retrieve the list of links for the given chat space ID
    final List<Link> links = linkRepository.findByChatSpaceId(chatSpaceId);
    // Convert the list of Link objects to LinkResponse objects and return
    return linkMapper.toLinkResponses(links);
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

  /**
   * Updates the links associated with a specific chat space.
   *
   * <p>This method retrieves the chat space and verifies that the user is either the creator or an admin of the chat space.
   * It then processes the provided list of link updates: if the link type already exists, it updates the URL; otherwise,
   * it creates a new link and saves it to the repository. Finally, it returns a localized response indicating the update was successful.
   *
   * @param updateLinkDto the data transfer object containing the links to be updated
   * @param user the user requesting the update, used to verify their authority over the chat space
   * @return a localized {@code UpdateLinkResponse} indicating the result of the update operation
   * @throws ChatSpaceNotFoundException if the specified chat space cannot be found
   * @throws FailedOperationException if the operation fails due to any other reason
   */
  @Override
  @Transactional
  public LinkUpdateResponse updateLink(final UpdateLinkDto updateLinkDto, final RegisteredUser user) throws ChatSpaceNotFoundException, FailedOperationException {
    // Retrieve the chat space using the provided chat space ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(updateLinkDto.getChatSpaceId());
    // Verify if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());

    // Fetch the existing links associated with the chat space
    final List<Link> existingLinks = linkRepository.findByChatSpaceId(updateLinkDto.getChatSpaceId());
    // Create a map of existing links by link type for easier lookup
    final Map<LinkType, Link> existingLinksMap = existingLinks.stream()
      .collect(Collectors.toMap(Link::getLinkType, link -> link));

    // Create a set of incoming link types for comparison
    final Set<LinkType> incomingLinkTypes = updateLinkDto.getValidLinkTypes();
    // Process each link from the update request
    upsertLinks(updateLinkDto.getLinks(), existingLinksMap, chatSpace);
    // Remove stale or old links
    removeStaleLinks(existingLinks, incomingLinkTypes);
    // Return a localized response indicating the update was successful
    return localizer.of(LinkUpdateResponse.of());
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
  protected void removeStaleLinks(final List<Link> existingLinks, final Set<LinkType> incomingLinkTypes) {
    if (nonNull(existingLinks) && nonNull(incomingLinkTypes)) {
      // Remove links that are no longer present in the incoming update
      for (final Link existingLink : existingLinks) {
        if (!incomingLinkTypes.contains(existingLink.getLinkType())) {
          linkRepository.delete(existingLink);
        }
      }
    }
  }

  /**
   * Upserts links by either updating existing ones or creating new ones based on the provided link DTOs.
   *
   * <p>This method processes each link DTO in the provided list. If a link type already exists in the
   * provided map of existing links, it updates the URL. If the link type doesn't exist, a new link is created
   * and saved to the repository.</p>
   *
   * @param linksDto A list of link DTOs to be processed, each containing link type and URL.
   * @param existingLinksMap A map of existing links, keyed by link type, to check for existing links.
   * @param chatSpace The chat space to associate new links with.
   */
  protected void upsertLinks(final List<LinkDto> linksDto, final Map<LinkType, Link> existingLinksMap, final ChatSpace chatSpace) {
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
        final Link newLink = dto.toLink(chatSpace);
        linkRepository.save(newLink);
      }
    }
  }

  /**
   * Deletes links associated with a specific chat space.
   *
   * <p>This method retrieves the chat space and verifies that the user is either the creator or an admin of the chat space.
   * It then finds all the links matching the specified chat space ID and link types, deletes them, and returns a localized response
   * indicating the success of the operation.
   *
   * @param deleteLinkDto the data transfer object containing the chat space ID and link types to be deleted
   * @param user the user requesting the deletion, used to verify their authority over the chat space
   * @return a localized {@code DeleteLinkResponse} indicating the result of the deletion operation
   * @throws ChatSpaceNotFoundException if the specified chat space cannot be found
   * @throws FailedOperationException if the operation fails due to any other reason
   */
  @Override
  @Transactional
  public LinkDeleteResponse deleteLink(final DeleteLinkDto deleteLinkDto, final RegisteredUser user) throws ChatSpaceNotFoundException, FailedOperationException {
    // Retrieve the chat space using the provided chat space ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(deleteLinkDto.getChatSpaceId());
    // Verify if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
    // Find all the links associated with the chat space and the specified link types
    final Collection<Link> links = linkRepository.findByChatSpaceIdAndLinkType(deleteLinkDto.getChatSpaceId(), deleteLinkDto.getLinkTypes());
    // Delete the found links from the repository
    linkRepository.deleteAll(links);
    // Return a localized response indicating the deletion was successful
    return localizer.of(LinkDeleteResponse.of());
  }

  /**
   * Sets the links that are updatable by the specified user in the given chat space.
   *
   * <p>This method first checks if the list of links is not null or empty and if the user is provided.
   * It then verifies if the user is an admin or the creator of the chat space. Based on this verification,
   * the method sets whether the links are updatable by the user.
   *
   * @param chatSpace the chat space whose links' updatability will be checked
   * @param links the list of {@code LinkResponse} objects to be updated
   * @param user the user whose permissions will be checked for updating the links
   */
  protected void setLinksThatAreUpdatableByUser(final ChatSpace chatSpace, final List<LinkResponse> links, final RegisteredUser user) {
    // Check if links and user are not null, and if the list of links is not empty
    if (nonNull(links) && !links.isEmpty() && nonNull(user)) {
      // Verify if the user is an admin or the creator of the chat space
      final boolean isAdmin = chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user.toMember());
      // Set whether the links are updatable by the user based on their admin status
      setLinksThatAreUpdatableByUser(links, isAdmin);
    }
  }

  /**
   * Marks the links as updatable if the user is an admin.
   *
   * <p>This method iterates over the provided list of links and marks them as updatable if the user is an admin.
   * It performs the check only if the list of links is not null or empty and the user is verified as an admin.
   *
   * @param links the list of {@code LinkResponse} objects to be updated
   * @param isAdmin a boolean indicating whether the user is an admin
   */
  protected static void setLinksThatAreUpdatableByUser(final List<LinkResponse> links, final boolean isAdmin) {
    // Check if the links are not null or empty and if the user is an admin
    if (nonNull(links) && !links.isEmpty() && isAdmin) {
      // Iterate through the links and mark each as updatable
      links.stream()
        .filter(Objects::nonNull)
        .forEach(LinkResponse::markAsUpdatable);
    }
  }

}
