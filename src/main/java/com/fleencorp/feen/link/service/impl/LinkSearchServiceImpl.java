package com.fleencorp.feen.link.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.service.BusinessOperationService;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.mapper.LinkMapper;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.holder.LinkParentDetailHolder;
import com.fleencorp.feen.link.model.info.LinkTypeInfo;
import com.fleencorp.feen.link.model.info.MusicLinkTypeInfo;
import com.fleencorp.feen.link.model.request.LinkSearchRequest;
import com.fleencorp.feen.link.model.response.availability.GetAvailableLinkTypeResponse;
import com.fleencorp.feen.link.model.response.availability.GetAvailableMusicLinkTypeResponse;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.link.model.search.LinkSearchResult;
import com.fleencorp.feen.link.repository.LinkRepository;
import com.fleencorp.feen.link.service.LinkSearchService;
import com.fleencorp.feen.model.contract.HasLinks;
import com.fleencorp.feen.stream.constant.common.MusicLinkType;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setLinksThatAreUpdatableByUser;
import static java.util.Objects.nonNull;

@Service
public class LinkSearchServiceImpl implements LinkSearchService {

  private final BusinessOperationService businessOperationService;
  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final LinkRepository linkRepository;
  private final LinkMapper linkMapper;
  private final Localizer localizer;

  public LinkSearchServiceImpl(
      final BusinessOperationService businessOperationService,
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final LinkRepository linkRepository,
      final LinkMapper linkMapper,
      final Localizer localizer) {
    this.businessOperationService = businessOperationService;
    this.chatSpaceOperationsService = chatSpaceOperationsService;
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
            lt -> LinkTypeInfo.of(lt, lt.getValue(), lt.getBusinessFormat(), lt.getCommunityFormat()
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
   * Retrieves and processes links for a specified parent entity.
   *
   * <p>This method queries the database for {@link Link} entities associated with the given
   * {@link LinkParentType} and parent ID. The results are mapped into {@link LinkResponse}
   * objects, enriched with updatable flags if the requesting {@link Member} has administrative
   * privileges. The final results are wrapped in a {@link LinkSearchResult} that includes
   * pagination details.</p>
   *
   * <p>The page size of the {@link LinkSearchRequest} is updated to a maximum of 1000 before
   * executing the query.</p>
   *
   * @param searchRequest the {@link LinkSearchRequest} containing the parent ID, parent type,
   *                      and pagination details
   * @param user          the {@link RegisteredUser} requesting the links; used to derive the
   *                      {@link Member} for admin checks
   * @return a {@link LinkSearchResult} containing the search results, including links, pagination
   *         information, and administrative flags; localized before being returned
   */
  @Override
  @Transactional(readOnly = true)
  public LinkSearchResult findLinks(final LinkSearchRequest searchRequest, final RegisteredUser user) {
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();
    final Long parentId = searchRequest.getParentId();
    final LinkParentType linkParentType = searchRequest.getParentType();

    searchRequest.updatePageSize(1000);
    final LinkParentDetailHolder linkParentDetailHolder = findLinkParent(parentId, linkParentType, member);
    final Page<Link> page = findLinksByParent(parentId, linkParentType, pageable);

    final Collection<LinkResponse> responses = linkMapper.toLinkResponses(page.getContent());
    setLinksThatAreUpdatableByUser(responses, linkParentDetailHolder.isAdmin());

    final SearchResult searchResult = toSearchResult(responses, page);
    final LinkSearchResult result = LinkSearchResult.of(searchResult, parentId);
    return localizer.of(result);
  }

  /**
   * Retrieves a paginated list of {@link Link} entities for the specified parent entity.
   *
   * <p>This method queries the {@link LinkRepository} for links associated with the given
   * {@code parentId} based on the provided {@link LinkParentType}. Supported parent types
   * include {@link LinkParentType#BUSINESS} and {@link LinkParentType#CHAT_SPACE}.</p>
   *
   * <p>If the {@code linkParentType} is not supported, this method returns an empty {@link Page}.</p>
   *
   * @param parentId       the ID of the parent entity whose links should be retrieved
   * @param linkParentType the type of parent entity (e.g., business or chat space)
   * @param pageable       pagination information for retrieving results
   * @return a {@link Page} of {@link Link} entities associated with the given parent ID;
   *         an empty page if the parent type is not supported
   */
  private Page<Link> findLinksByParent(final Long parentId, final LinkParentType linkParentType, final Pageable pageable) {
    return switch (linkParentType) {
      case BUSINESS -> linkRepository.findByBusinessId(parentId, pageable);
      case CHAT_SPACE ->  linkRepository.findByChatSpaceId(parentId, pageable);
      default -> Page.empty();
    };
  }

  /**
   * Retrieves the parent entity details for a given parent ID and {@link LinkParentType}.
   *
   * <p>This method determines whether the parent is a {@link Business} or a {@link ChatSpace},
   * and fetches the corresponding entity using the appropriate service. It also checks whether
   * the provided {@link Member} has administrative rights over the parent entity.</p>
   *
   * <p>If the {@code linkParentType} is {@link LinkParentType#STREAM} or {@link LinkParentType#USER},
   * a {@link FailedOperationException} is thrown.</p>
   *
   * @param parentId       the ID of the parent entity
   * @param linkParentType the type of parent entity (e.g., business or chat space)
   * @param member         the {@link Member} for which administrative rights are evaluated
   * @return a {@link LinkParentDetailHolder} containing the resolved parent entity details and admin status
   * @throws FailedOperationException if {@code parentId} or {@code linkParentType} is {@code null},
   *                                  or if the {@code linkParentType} is {@link LinkParentType#STREAM}
   *                                  or {@link LinkParentType#USER}
   */
  private LinkParentDetailHolder findLinkParent(final Long parentId, final LinkParentType linkParentType, final Member member) {
    checkIsNullAny(List.of(parentId, linkParentType), FailedOperationException::new);

    switch (linkParentType) {
      case STREAM, USER: throw FailedOperationException.of();
    }

    final Business business = LinkParentType.isBusiness(linkParentType) ? businessOperationService.findBusiness(parentId) : null;
    final ChatSpace chatSpace = LinkParentType.isChatSpace(linkParentType) ? chatSpaceOperationsService.findChatSpace(parentId) : null;

    boolean isAdmin = false;
    switch (linkParentType) {
      case BUSINESS: isAdmin = nonNull(business) && business.checkIsOwner(parentId);
      case CHAT_SPACE: isAdmin = chatSpaceOperationsService.checkIsAdmin(chatSpace, member);
    }

    return LinkParentDetailHolder.of(business, chatSpace, linkParentType, isAdmin);
  }

  /**
   * Retrieves and assigns the parent links for the specified entity based on its {@link LinkParentType}.
   *
   * <p>This method queries the {@link LinkRepository} to fetch links associated with the given
   * parent entity (e.g., business or chat space), maps them to {@link LinkResponse} objects,
   * and assigns them to the parent via {@link HasLinks}.</p>
   *
   * <p>If the {@code parent} or {@code linkParentType} is {@code null}, a {@link FailedOperationException}
   * is thrown. The resulting links are stored as a {@link HashSet} to avoid duplicates.</p>
   *
   * @param parent         the parent entity implementing {@link HasLinks}, whose links are to be retrieved
   * @param linkParentType the type of parent entity (e.g., business, chat space) for which links should be fetched
   * @param <T>            a type extending {@link HasLinks}
   * @throws FailedOperationException if {@code parent} or {@code linkParentType} is {@code null}
   */
  @Override
  @Transactional(readOnly = true)
  public <T extends HasLinks> void findAndSetParentLinks(final T parent, final LinkParentType linkParentType) {
    checkIsNullAny(List.of(parent, linkParentType), FailedOperationException::new);

    final Long parentId = parent.getNumberId();
    List<Link> links = new ArrayList<>();

    switch (linkParentType) {
      case BUSINESS: links = linkRepository.findByBusiness(parentId, linkParentType);
      case CHAT_SPACE: links = linkRepository.findByChatSpace(parentId, linkParentType);
    }

    final Collection<LinkResponse> linkResponses = linkMapper.toLinkResponses(links);
    parent.setLinks(new HashSet<>(linkResponses));
  }
}
