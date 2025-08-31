package com.fleencorp.feen.chat.space.service.impl.core;

import com.fleencorp.feen.bookmark.service.BookmarkOperationService;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.projection.ChatSpaceMemberSelect;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOtherService;
import com.fleencorp.feen.chat.space.service.member.ChatSpaceMemberOperationsService;
import com.fleencorp.feen.like.service.LikeOperationService;
import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.service.LinkSearchService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.contract.HasId;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.determineIfUserIsTheOrganizerOfEntity;
import static com.fleencorp.feen.common.util.common.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

@Service
public class ChatSpaceOtherServiceImpl implements ChatSpaceOtherService {

  private static final int DEFAULT_NUMBER_OF_MEMBERS_TO_GET_FOR_CHAT_SPACE = 10;

  private final BookmarkOperationService bookmarkOperationService;
  private final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService;
  private final LikeOperationService likeOperationService;
  private final LinkSearchService linkSearchService;
  private final UnifiedMapper unifiedMapper;

  public ChatSpaceOtherServiceImpl(
      final BookmarkOperationService bookmarkOperationService,
      final ChatSpaceMemberOperationsService chatSpaceMemberOperationsService,
      final LikeOperationService likeOperationService,
      final LinkSearchService linkSearchService,
      final UnifiedMapper unifiedMapper) {
    this.bookmarkOperationService = bookmarkOperationService;
    this.chatSpaceMemberOperationsService = chatSpaceMemberOperationsService;
    this.likeOperationService = likeOperationService;
    this.linkSearchService = linkSearchService;
    this.unifiedMapper = unifiedMapper;
  }

  /**
   * Processes additional details for the given list of chat space responses, including membership status
   * and organizer determination.
   *
   * <p>This method first checks that the provided list of chat space responses and user are not null.
   * It then retrieves the user's membership status for the chat spaces and processes each response
   * by setting membership details, retrieving recent members, and determining if the user is the organizer.</p>
   *
   * @param chatSpacesResponses the list of chat space responses to process
   * @param user                the user whose membership and organizer status are to be determined
   */
  @Override
  public void processOtherChatSpaceDetails(final List<ChatSpaceResponse> chatSpacesResponses, final RegisteredUser user) {
    if (allNonNull(chatSpacesResponses, user, user.toMember()) && !chatSpacesResponses.isEmpty()) {
      final Member member = user.toMember();
      final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap = getUserMembershipDetailsMap(chatSpacesResponses, user);

      bookmarkOperationService.populateChatSpaceBookmarksFor(chatSpacesResponses, member);
      likeOperationService.populateChatSpaceLikesFor(chatSpacesResponses, member);

      chatSpacesResponses.stream()
        .filter(Objects::nonNull)
        .forEach(chatSpaceResponse -> processChatSpaceResponse(chatSpaceResponse, membershipDetailsMap, user));
    }
  }

  /**
   * Retrieves the user's membership status for the given list of chat spaces and returns it as a map.
   *
   * <p>This method extracts the chat space IDs from the provided {@code chatSpacesResponses},
   * fetches the user's membership details for those chat spaces from the repository,
   * and groups the membership status by chat space ID.</p>
   *
   * @param chatSpacesResponses the list of chat space responses to process
   * @param user                the user whose membership status is to be retrieved
   * @return a map where the keys are chat space IDs and the values are the corresponding membership status
   */
  protected Map<Long, ChatSpaceMemberSelect> getUserMembershipDetailsMap(final List<ChatSpaceResponse> chatSpacesResponses, final RegisteredUser user) {
    final List<Long> chatSpaceIds = HasId.getIds(chatSpacesResponses);
    final Member member = user.toMember();

    final List<ChatSpaceMemberSelect> userMemberships = chatSpaceMemberOperationsService.findByMemberAndChatSpaceIds(member, chatSpaceIds);
    return HasId.groupMembershipByEntriesId(userMemberships);
  }

  /**
   * Processes the given {@code ChatSpaceResponse} by setting membership status, recent members,
   * and determining if the given user is the organizer.
   *
   * <p>This method sets the membership status of the chat space using the provided {@code membershipStatusMap}.
   * It retrieves and assigns some of the most recent approved members to the chat space response.
   * Additionally, it determines whether the given user is the organizer of the chat space.</p>
   *
   * @param chatSpaceResponse    the chat space response object to process
   * @param membershipDetailsMap  a map containing membership status information, keyed by chat space ID
   * @param user                 the user whose organizer status is to be determined
   */
  protected void processChatSpaceResponse(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap, final RegisteredUser user) {
    setMembershipDetails(chatSpaceResponse, membershipDetailsMap);

    setSomeRecentChatSpaceMembers(chatSpaceResponse);
    setChatSpaceThatAreUpdatableByUser(chatSpaceResponse, membershipDetailsMap);

    linkSearchService.findAndSetParentLinks(chatSpaceResponse, LinkParentType.CHAT_SPACE);
    determineIfUserIsTheOrganizerOfEntity(chatSpaceResponse, user.toMember());
  }

  /**
   * Sets the membership status for the given {@code ChatSpaceResponse} using the provided membership status map.
   *
   * <p>This method retrieves the membership details for the chat space from the given {@code membershipStatusMap}
   * using the {@code numberId} of the {@code chatSpaceResponse}. If a matching entry is found, it updates
   * the chat space response with membership-related information.</p>
   *
   * @param chatSpaceResponse    the chat space response object to update with membership details
   * @param membershipDetailsMap  a map containing membership status information, keyed by chat space ID
   */
  protected void setMembershipDetails(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap) {
    final ChatSpaceMemberSelect membershipDetail = membershipDetailsMap.get(chatSpaceResponse.getNumberId());

    Optional.ofNullable(membershipDetail)
      .ifPresent(membership -> unifiedMapper.setMembershipInfo(
        chatSpaceResponse,
        membership.getRequestToJoinStatus(),
        membership.getJoinStatus(),
        membership.getRole(),
        membership.isAMember(),
        membership.isAdmin(),
        membership.hasLeft(),
        membership.isRemoved()
      ));
  }

  /**
   * Sets a subset of recently approved members for the given {@code ChatSpaceResponse}.
   *
   * <p>This method retrieves a limited number of active members from the chat space, converts them into
   * {@code ChatSpaceMemberResponse} objects, and assigns them as a set to the {@code someMembers} field of
   * the provided {@code ChatSpaceResponse}.</p>
   *
   * @param chatSpaceResponse the chat space response object to which the recent members will be assigned
   */
  protected void setSomeRecentChatSpaceMembers(final ChatSpaceResponse chatSpaceResponse) {
    final Long chatSpaceId = chatSpaceResponse.getNumberId();
    final ChatSpace chatSpace = ChatSpace.of(chatSpaceId);
    final Pageable pageable = PageRequest.of(0, DEFAULT_NUMBER_OF_MEMBERS_TO_GET_FOR_CHAT_SPACE);

    final Page<ChatSpaceMember> page = chatSpaceMemberOperationsService.findActiveChatSpaceMembers(chatSpace, ChatSpaceRequestToJoinStatus.APPROVED, pageable);
    final List<ChatSpaceMemberResponse> chatSpaceMemberResponses = unifiedMapper.toChatSpaceMemberResponsesPublic(page.getContent());
    final Set<ChatSpaceMemberResponse> chatSpaceMemberResponsesSet = new HashSet<>(chatSpaceMemberResponses);

    chatSpaceResponse.setSomeMembers(chatSpaceMemberResponsesSet);
  }

  /**
   * Determines if a given chat space response should be marked as updatable based on
   * the user's membership status and role.
   *
   * <p>This method checks whether the user associated with the provided membership status
   * map is an admin of the chat space. If so, it marks the {@code ChatSpaceResponse}
   * as updatable.</p>
   *
   * @param chatSpaceResponse The response object representing the chat space to potentially mark as updatable.
   * @param membershipDetailsMap A map of chat space IDs to corresponding membership status objects.
   */
  protected static void setChatSpaceThatAreUpdatableByUser(final ChatSpaceResponse chatSpaceResponse, final Map<Long, ChatSpaceMemberSelect> membershipDetailsMap) {
    final ChatSpaceMemberSelect membershipStatus = membershipDetailsMap.get(chatSpaceResponse.getNumberId());

    if (nonNull(membershipStatus) && membershipStatus.isAdmin()) {
      chatSpaceResponse.markAsUpdatable();
    }
  }

}
