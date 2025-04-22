package com.fleencorp.feen.service.impl.link;

import com.fleencorp.feen.constant.link.LinkType;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.mapper.link.LinkMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.other.Link;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.dto.link.DeleteLinkDto;
import com.fleencorp.feen.model.dto.link.UpdateLinkDto;
import com.fleencorp.feen.model.dto.link.UpdateStreamMusicLinkDto;
import com.fleencorp.feen.model.request.search.LinkSearchRequest;
import com.fleencorp.feen.model.response.link.DeleteLinkResponse;
import com.fleencorp.feen.model.response.link.LinkResponse;
import com.fleencorp.feen.model.response.link.UpdateLinkResponse;
import com.fleencorp.feen.model.response.link.UpdateStreamMusicLinkResponse;
import com.fleencorp.feen.model.search.link.LinkSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.link.LinkRepository;
import com.fleencorp.feen.repository.stream.StreamRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.link.LinkService;
import com.fleencorp.feen.service.stream.common.StreamService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.model.dto.link.UpdateLinkDto.LinkDto;
import static java.util.Objects.nonNull;

@Service
public class LinkServiceImpl implements LinkService {

  private final ChatSpaceService chatSpaceService;
  private final StreamService streamService;
  private final LinkRepository linkRepository;
  private final StreamRepository streamRepository;
  private final LinkMapper linkMapper;
  private final Localizer localizer;

  public LinkServiceImpl(
      final ChatSpaceService chatSpaceService,
      final StreamService streamService,
      final LinkRepository linkRepository,
      final StreamRepository streamRepository,
      final LinkMapper linkMapper,
      final Localizer localizer) {
    this.chatSpaceService = chatSpaceService;
    this.streamService = streamService;
    this.linkRepository = linkRepository;
    this.streamRepository = streamRepository;
    this.linkMapper = linkMapper;
    this.localizer = localizer;
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
  public LinkSearchResult findLinks(final LinkSearchRequest searchRequest, final FleenUser user) {
    // Update the page size to 1000 for the search request
    searchRequest.updatePageSize(1000);
    // Initialize an empty page of links
    Page<Link> page = Page.empty();

    // Check if the search request is related to a chat space and fetch the corresponding links
    if (searchRequest.isChatSpaceSearchRequest()) {
      page = linkRepository.findByChatSpaceId(searchRequest.getChatSpaceId(), searchRequest.getPage());
    }

    // Retrieve the chat space details for the given chat space ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(searchRequest.getChatSpaceId());
    // Map the links to their response objects
    final List<LinkResponse> views = linkMapper.toLinkResponses(page.getContent());
    // Set links that are updatable by the user
    setLinksThatAreUpdatableByUser(chatSpace, views, user);
    // Create the search result object with the views and pagination info
    final LinkSearchResult searchResult = LinkSearchResult.of(toSearchResult(views, page), searchRequest.getParentId());

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
  public UpdateStreamMusicLinkResponse updateStreamMusicLink(final UpdateStreamMusicLinkDto updateStreamMusicLinkDto, final FleenUser user) {
    // Retrieve the stream object based on the stream ID
    final FleenStream stream = streamService.findStream(updateStreamMusicLinkDto.getStreamId());
    // Ensure the user is the organizer of the stream
    stream.checkIsOrganizer(user.getId());
    // Validate the provided music link
    updateStreamMusicLinkDto.checkLinkIsValid();
    // Update the stream's music link
    stream.setMusicLink(updateStreamMusicLinkDto.getMusicLink());
    // Save the updated stream to the repository
    streamRepository.save(stream);
    // Return a localized response indicating the update was successful
    return localizer.of(UpdateStreamMusicLinkResponse.of());
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
  public UpdateLinkResponse updateLink(final UpdateLinkDto updateLinkDto, final FleenUser user) throws ChatSpaceNotFoundException, FailedOperationException {
    // Retrieve the chat space using the provided chat space ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(updateLinkDto.getChatSpaceId());
    // Verify if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user);

    // Fetch the existing links associated with the chat space
    final List<Link> existingLinks = linkRepository.findByChatSpaceId(updateLinkDto.getChatSpaceId());
    // Create a map of existing links by link type for easier lookup
    final Map<LinkType, Link> existingLinksMap = existingLinks.stream()
      .collect(Collectors.toMap(Link::getLinkType, link -> link));

    // Process each link from the update request
    for (final LinkDto dto : updateLinkDto.getLinks()) {
      final LinkType linkType = dto.getLinkType();
      final String url = dto.getUrl();

      // Skip invalid link DTOs
      if (dto.isInvalid()) {
        continue;
      }

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

    // Return a localized response indicating the update was successful
    return localizer.of(UpdateLinkResponse.of());
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
  public DeleteLinkResponse deleteLink(final DeleteLinkDto deleteLinkDto, final FleenUser user) throws ChatSpaceNotFoundException, FailedOperationException {
    // Retrieve the chat space using the provided chat space ID
    final ChatSpace chatSpace = chatSpaceService.findChatSpace(deleteLinkDto.getChatSpaceId());
    // Verify if the user is the creator or an admin of the chat space
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user);
    // Find all the links associated with the chat space and the specified link types
    final List<Link> links = linkRepository.findByChatSpaceIdAndLinkType(deleteLinkDto.getChatSpaceId(), deleteLinkDto.getLinkTypes());
    // Delete the found links from the repository
    linkRepository.deleteAll(links);

    // Return a localized response indicating the deletion was successful
    return localizer.of(DeleteLinkResponse.of());
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
  protected void setLinksThatAreUpdatableByUser(final ChatSpace chatSpace, final List<LinkResponse> links, final FleenUser user) {
    // Check if links and user are not null, and if the list of links is not empty
    if (nonNull(links) && !links.isEmpty() && nonNull(user)) {
      // Verify if the user is an admin or the creator of the chat space
      final boolean isAdmin = chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, user);
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
