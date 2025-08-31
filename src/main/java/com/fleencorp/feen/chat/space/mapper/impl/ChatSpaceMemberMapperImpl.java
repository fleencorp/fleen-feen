package com.fleencorp.feen.chat.space.mapper.impl;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.chat.space.constant.member.ChatSpaceMemberRole;
import com.fleencorp.feen.chat.space.constant.membership.IsAChatSpaceAdmin;
import com.fleencorp.feen.chat.space.constant.membership.IsAChatSpaceMember;
import com.fleencorp.feen.chat.space.constant.membership.IsChatSpaceMemberLeft;
import com.fleencorp.feen.chat.space.constant.membership.IsChatSpaceMemberRemoved;
import com.fleencorp.feen.chat.space.mapper.ChatSpaceMemberMapper;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import com.fleencorp.feen.chat.space.model.info.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.chat.space.model.info.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.chat.space.model.info.membership.*;
import com.fleencorp.feen.chat.space.model.response.core.ChatSpaceResponse;
import com.fleencorp.feen.chat.space.model.response.member.base.ChatSpaceMemberResponse;
import com.fleencorp.feen.common.constant.common.JoinStatus;
import com.fleencorp.feen.common.model.info.JoinStatusInfo;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * This class is responsible for mapping ChatSpaceMember entities to
 * corresponding {@link ChatSpaceMemberResponse} DTOs, including the translation of message codes
 * based on the current locale. It facilitates the conversion of domain
 * objects into API response formats for chat space member information,
 * including details like join status, roles, and request status.
 *
 * <p>The class uses a {@link MessageSource} to provide internationalized
 * messages for role and status information, ensuring that the responses
 * are localized based on the user's locale.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class ChatSpaceMemberMapperImpl extends BaseMapper implements ChatSpaceMemberMapper {

  /**
   * Constructor for the ChatSpaceMemberMapper class.
   *
   * @param messageSource the MessageSource used for message translation
   */
  public ChatSpaceMemberMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Converts a given {@link ChatSpaceMember} and {@link ChatSpace} into a {@link ChatSpaceMemberResponse}.
   *
   * <p>This method takes a chat space member entity (`ChatSpaceMember`) and a chat space entity
   * (`ChatSpace`), processes relevant information, and returns a detailed `ChatSpaceMemberResponse`.
   * The response includes details like member ID, name, role, membership status, and other
   * membership-related information such as whether the member has left or been removed from the
   * chat space.</p>
   *
   * <p>The method first sets basic details such as the member's ID, name, and role.
   * Then, it creates status info objects to represent the member's request to join status,
   * membership status, and admin status. These statuses are translated into localized messages
   * using the `translate()` method, ensuring that the response can adapt to different languages
   * or locales if needed.</p>
   *
   * <p>Finally, the method composes all the status-related info into a `ChatSpaceMembershipInfo`
   * object and assigns it to the response.</p>
   *
   * @param entry The {@link ChatSpaceMember} entity containing the member details.
   * @param chatSpace The {@link ChatSpace} entity related to the member.
   * @return A {@link ChatSpaceMemberResponse} containing all relevant member details.
   */
  @Override
  public ChatSpaceMemberResponse toChatSpaceMemberResponse(final ChatSpaceMember entry, final ChatSpace chatSpace) {
    if (nonNull(entry)) {

      final ChatSpaceMemberResponse response = new ChatSpaceMemberResponse();
      response.setChatSpaceMemberId(entry.getChatSpaceMemberId());
      response.setMemberName(entry.getFullName());
      response.setUsername(entry.getUsername());

      final ChatSpaceMembershipInfo membershipInfo = getMembershipInfo(entry, chatSpace);
      response.setMembershipInfo(membershipInfo);

      response.setOrganizerId(chatSpace.getOrganizerId());

      return response;
    }
    return null;
  }

  /**
   * Converts a {@link ChatSpaceMember} object to a {@link ChatSpaceMemberResponse} for public view.
   *
   * <p>This method takes a {@code ChatSpaceMember} entry, extracts relevant fields such as the member ID, username,
   * and profile photo, and maps them to a {@link ChatSpaceMemberResponse} object. If the entry is null, it returns {@code null}.</p>
   *
   * @param entry the {@link ChatSpaceMember} object to convert
   * @return a {@link ChatSpaceMemberResponse} containing the member's public details, or {@code null} if the entry is null
   */
  protected ChatSpaceMemberResponse toChatSpaceMemberResponsePublic(final ChatSpaceMember entry) {
    if (nonNull(entry)) {
      final ChatSpaceMemberResponse response = new ChatSpaceMemberResponse();

      response.setChatSpaceMemberId(entry.getChatSpaceMemberId());
      response.setUsername(entry.getUsername());
      response.setDisplayPhoto(entry.getProfilePhoto());

      return response;
    }
    return null;
  }

  /**
   * Retrieves detailed membership information for a specific member in a chat space.
   * This method processes various statuses and roles associated with the member
   * and the chat space, and returns an aggregated {@link ChatSpaceMembershipInfo}.
   *
   * @param entry the {@link ChatSpaceMember} object representing the member whose membership info is being retrieved.
   *              Contains information about the member's request to join, membership status, role, and more.
   * @param chatSpace the {@link ChatSpace} object representing the chat space in which the membership information
   *                  is being queried.
   *                  Provides information such as the visibility of the chat space.
   * @return a {@link ChatSpaceMembershipInfo} object containing comprehensive membership information including:
   *         - Request to join status and associated message(s)
   *         - Membership and administrative roles
   *         - Removal, leaving, and membership statuses
   */
  @Override
  public ChatSpaceMembershipInfo getMembershipInfo(final ChatSpaceMember entry, final ChatSpace chatSpace) {
    final ChatSpaceRequestToJoinStatus requestToJoinStatus = entry.getRequestToJoinStatus();
    final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));

    final JoinStatus joinStatus = JoinStatus.getJoinStatus(entry.getRequestToJoinStatus(), chatSpace.getSpaceVisibility(), entry.isAMember(), entry.hasLeft(), entry.isRemoved());
    final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));

    final IsAChatSpaceMember isAChatSpaceMember = IsAChatSpaceMember.by(entry.isAMember());
    final IsAChatSpaceMemberInfo isAChatSpaceMemberInfo = IsAChatSpaceMemberInfo.of(entry.isAMember(), translate(isAChatSpaceMember.getMessageCode()));

    final IsAChatSpaceAdmin isAChatSpaceAdmin = IsAChatSpaceAdmin.by(entry.isAdmin());
    final IsAChatSpaceAdminInfo isAChatSpaceAdminInfo = IsAChatSpaceAdminInfo.of(entry.isAdmin(), translate(isAChatSpaceAdmin.getMessageCode()), translate(isAChatSpaceAdmin.getMessageCode2()));

    final IsChatSpaceMemberRemoved isChatSpaceMemberRemoved = IsChatSpaceMemberRemoved.by(entry.isRemoved());
    final IsChatSpaceMemberRemovedInfo isChatSpaceMemberRemovedInfo = IsChatSpaceMemberRemovedInfo.of(entry.isRemoved(), translate(isChatSpaceMemberRemoved.getMessageCode()));

    final IsChatSpaceMemberLeft isChatSpaceMemberLeft = IsChatSpaceMemberLeft.by(entry.hasLeft());
    final IsChatSpaceMemberLeftInfo isChatSpaceMemberLeftInfo = IsChatSpaceMemberLeftInfo.of(entry.hasLeft(), translate(isChatSpaceMemberLeft.getMessageCode()));

    final ChatSpaceMemberRole role = entry.getRole();
    final ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo = toMemberRoleInfo(role);

    return ChatSpaceMembershipInfo.of(
      requestToJoinStatusInfo,
      joinStatusInfo,
      chatSpaceMemberRoleInfo,
      isAChatSpaceMemberInfo,
      isAChatSpaceAdminInfo,
      isChatSpaceMemberLeftInfo,
      isChatSpaceMemberRemovedInfo
    );
  }

  /**
   * Converts a list of {@link ChatSpaceMember} entries to a list of {@link ChatSpaceMemberResponse}.
   *
   * <p>This method processes a list of {@code ChatSpaceMember} objects, filtering out null entries
   * and mapping the non-null ones to {@code ChatSpaceMemberResponse}. If the input list is null,
   * it returns an empty list.</p>
   *
   * @param entries the list of {@code ChatSpaceMember} objects to be converted; can be {@code null}
   * @return a list of {@code ChatSpaceMemberResponse} objects built from the given {@code ChatSpaceMember} list,
   *         or an empty list if the input list is null or contains no valid entries
   */
  @Override
  public List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(final List<ChatSpaceMember> entries, final ChatSpace chatSpace) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(chatSpaceMember -> this.toChatSpaceMemberResponse(chatSpaceMember, chatSpace))
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a list of {@link ChatSpaceMember} objects to a list of {@link ChatSpaceMemberResponse} objects for public view.
   *
   * <p>This method processes the provided list of {@code ChatSpaceMember} entries by filtering out any null values
   * and converting each entry to a {@link ChatSpaceMemberResponse} for public consumption.
   * If the input list is null, an empty list is returned.</p>
   *
   * @param entries the list of {@link ChatSpaceMember} objects to convert
   * @return a list of {@link ChatSpaceMemberResponse} objects, or an empty list if the input is null
   */
  @Override
  public List<ChatSpaceMemberResponse> toChatSpaceMemberResponsesPublic(final List<ChatSpaceMember> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toChatSpaceMemberResponsePublic)
        .toList();
    }
    return List.of();
  }

  /**
   * Converts a ChatSpaceMemberRole object to a ChatSpaceMemberRoleInfo object.
   *
   * @param role the ChatSpaceMemberRole object to convert
   * @return a ChatSpaceMemberRoleInfo object containing the member's role and its translated message
   */
  @Override
  public ChatSpaceMemberRoleInfo toMemberRoleInfo(final ChatSpaceMemberRole role) {
    return ChatSpaceMemberRoleInfo.of(role, translate(role.getMessageCode()));
  }

  /**
   * Converts a {@link ChatSpaceRequestToJoinStatus} to a {@link ChatSpaceRequestToJoinStatusInfo}.
   *
   * <p>This method takes a {@link ChatSpaceResponse} and a {@link ChatSpaceRequestToJoinStatus}
   * as input and converts them into a {@link ChatSpaceRequestToJoinStatusInfo} object,
   * which contains information about the request to join the chat space.</p>
   *
   * <p>If both the `chatSpace` and `requestToJoinStatus` are non-null, the method constructs
   * a {@link ChatSpaceRequestToJoinStatusInfo} by providing the request status and its
   * localized message (translated via the status' message code).</p>
   *
   * <p>If either the `chatSpace` or `requestToJoinStatus` is null, the method returns {@code null}.</p>
   *
   * @param chatSpace the chat space response object
   * @param requestToJoinStatus the request to join status of the chat space
   * @return a {@link ChatSpaceRequestToJoinStatusInfo} object with the request status information,
   *         or {@code null} if either input parameter is null
   */
  @Override
  public ChatSpaceRequestToJoinStatusInfo toRequestToJoinStatusInfo(final ChatSpaceResponse chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    if (nonNull(chatSpace) && nonNull(requestToJoinStatus)) {
      return ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));
    }
    return null;
  }

  /**
   * Converts a {@link JoinStatus} to a {@link JoinStatusInfo}.
   *
   * <p>This method takes a {@link ChatSpaceResponse} and a {@link JoinStatus}
   * as input and converts them into a {@link JoinStatusInfo} object,
   * which contains information about the join status of the chat space.</p>
   *
   * <p>If both the `chatSpace` and `joinStatus` are non-null, the method constructs
   * a {@link JoinStatusInfo} by providing the join status along with its two
   * localized messages (translated via the status' message codes).</p>
   *
   * <p>If either the `chatSpace` or `joinStatus` is null, the method returns {@code null}.</p>
   *
   * @param chatSpace the chat space response object
   * @param joinStatus the join status of the chat space
   * @return a {@link JoinStatusInfo} object with the join status information,
   *         or {@code null} if either input parameter is null
   */
  @Override
  public JoinStatusInfo toJoinStatusInfo(final ChatSpaceResponse chatSpace, final JoinStatus joinStatus) {
    if (nonNull(chatSpace) && nonNull(joinStatus)) {
      return JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
    }
    return null;
  }

  /**
   * Converts a boolean representing membership status to an {@link IsAChatSpaceMemberInfo}.
   *
   * <p>This method takes a boolean flag `isAMember` that indicates whether the user is a member of
   * the chat space. It converts this flag into an {@link IsAChatSpaceMember} enum by using
   * {@link IsAChatSpaceMember#by(boolean)}.</p>
   *
   * <p>The method then constructs an {@link IsAChatSpaceMemberInfo} object, which contains
   * the membership status and its localized message (translated via the membership status' message code).</p>
   *
   * @param isAMember a boolean indicating if the user is a member of the chat space
   * @return an {@link IsAChatSpaceMemberInfo} object containing the membership status information
   */
  @Override
  public IsAChatSpaceMemberInfo toIsAChatSpaceMemberInfo(final boolean isAMember) {
    final IsAChatSpaceMember isAChatSpaceMember = IsAChatSpaceMember.by(isAMember);
    return IsAChatSpaceMemberInfo.of(isAMember, translate(isAChatSpaceMember.getMessageCode()));
  }

  /**
   * Converts a boolean representing admin status to an {@link IsAChatSpaceAdminInfo}.
   *
   * <p>This method takes a boolean flag `isAdmin` that indicates whether the user is an admin of
   * the chat space. It converts this flag into an {@link IsAChatSpaceAdmin} enum using
   * {@link IsAChatSpaceAdmin#by(boolean)}.</p>
   *
   * <p>The method then constructs an {@link IsAChatSpaceAdminInfo} object, which contains
   * the admin status and its two localized messages (translated via the admin status' message codes).</p>
   *
   * @param isAdmin a boolean indicating if the user is an admin of the chat space
   * @return an {@link IsAChatSpaceAdminInfo} object containing the admin status information
   */
  @Override
  public IsAChatSpaceAdminInfo toIsAChatSpaceAdminInfo(final boolean isAdmin) {
    final IsAChatSpaceAdmin isAChatSpaceAdmin = IsAChatSpaceAdmin.by(isAdmin);
    return IsAChatSpaceAdminInfo.of(isAdmin, translate(isAChatSpaceAdmin.getMessageCode()), translate(isAChatSpaceAdmin.getMessageCode2()));
  }

  /**
   * Converts a boolean representing whether a member has been removed from the chat space to an {@link IsChatSpaceMemberRemovedInfo}.
   *
   * <p>This method takes a boolean flag `isRemoved` that indicates whether the user has been removed
   * from the chat space. It converts this flag into an {@link IsChatSpaceMemberRemoved} enum using
   * {@link IsChatSpaceMemberRemoved#by(boolean)}.</p>
   *
   * <p>The method then constructs an {@link IsChatSpaceMemberRemovedInfo} object, which contains
   * the removal status and its localized message (translated via the removal status' message code).</p>
   *
   * @param isRemoved a boolean indicating if the user has been removed from the chat space
   * @return an {@link IsChatSpaceMemberRemovedInfo} object containing the removal status information
   */
  @Override
  public IsChatSpaceMemberRemovedInfo toIsChatSpaceMemberRemovedInfo(final boolean isRemoved) {
    final IsChatSpaceMemberRemoved isChatSpaceMemberRemoved = IsChatSpaceMemberRemoved.by(isRemoved);
    return IsChatSpaceMemberRemovedInfo.of(isRemoved, translate(isChatSpaceMemberRemoved.getMessageCode()));
  }

  /**
   * Converts a boolean representing whether a member has left the chat space to an {@link IsChatSpaceMemberLeftInfo}.
   *
   * <p>This method takes a boolean flag `hasLeft` that indicates whether the user has left the
   * chat space. It converts this flag into an {@link IsChatSpaceMemberLeft} enum using
   * {@link IsChatSpaceMemberLeft#by(boolean)}.</p>
   *
   * <p>The method then constructs an {@link IsChatSpaceMemberLeftInfo} object, which contains
   * the left status and its localized message (translated via the left status' message code).</p>
   *
   * @param hasLeft a boolean indicating if the user has left the chat space
   * @return an {@link IsChatSpaceMemberLeftInfo} object containing the left status information
   */
  @Override
  public IsChatSpaceMemberLeftInfo toIsChatSpaceMemberLeftInfo(final boolean hasLeft) {
    final IsChatSpaceMemberLeft isChatSpaceMemberLeft = IsChatSpaceMemberLeft.by(hasLeft);
    return IsChatSpaceMemberLeftInfo.of(hasLeft, translate(isChatSpaceMemberLeft.getMessageCode()));
  }
}
