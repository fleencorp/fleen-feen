package com.fleencorp.feen.mapper.impl.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.chat.space.membership.IsAChatSpaceAdmin;
import com.fleencorp.feen.constant.chat.space.membership.IsAChatSpaceMember;
import com.fleencorp.feen.constant.chat.space.membership.IsChatSpaceMemberLeft;
import com.fleencorp.feen.constant.chat.space.membership.IsChatSpaceMemberRemoved;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceVisibilityInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.*;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link ChatSpace} entities to their corresponding response DTOs.
 *
 * <p>This class provides static methods for converting {@code ChatSpace} objects and related entities
 * to response objects, facilitating the transformation of domain entities to DTOs used for API responses.</p>
 *
 * <p>It handles null checks and performs necessary data conversions to ensure the consistency
 * and validity of mapped data.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class ChatSpaceMapperImpl implements ChatSpaceMapper {

  private final MessageSource messageSource;

  /**
   * Constructs a {@code ChatSpaceMapper} with the specified {@link MessageSource}.
   *
   * <p>The {@link MessageSource} is used to retrieve localized messages
   * for various components of the chat space mapping process.</p>
   *
   * @param messageSource the source for localized messages; must not be {@code null}.
   */
  public ChatSpaceMapperImpl(
      final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Translates a message code into a localized message based on the current locale.
   *
   * <p>This method retrieves a message from the {@link MessageSource} using the provided
   * message code and the locale obtained from {@link LocaleContextHolder}.</p>
   *
   * @param messageCode the code of the message to be translated; must not be {@code null}.
   * @return the localized message corresponding to the given message code.
   */
  private String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }

  /**
   * Converts a {@link ChatSpace} entry to a {@link ChatSpaceResponse}.
   *
   * <p>This method takes a {@link ChatSpace} entity as input and converts it
   * into a {@link ChatSpaceResponse} object. It sets various fields such as
   * the chat space ID, title, description, tags, guidelines, and member count.</p>
   *
   * <p>Additionally, it retrieves both the masked and unmasked space links,
   * active status, and the creation and update timestamps.</p>
   *
   * <p>It also maps the chat space visibility to a {@link ChatSpaceVisibilityInfo}
   * object and sets the organizer's information (name, email, and phone).</p>
   *
   * <p>Finally, it sets the request-to-join status, and the join status,
   * including localized messages for both statuses.</p>
   *
   * @param entry the {@link ChatSpace} entity to convert
   * @return a {@link ChatSpaceResponse} object populated with chat space details,
   *         or {@code null} if the input is null
   */
  @Override
  public ChatSpaceResponse toChatSpaceResponse(final ChatSpace entry) {
    if (nonNull(entry)) {
      final ChatSpaceResponse response = new ChatSpaceResponse();
      response.setId(entry.getChatSpaceId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTags(entry.getTags());
      response.setGuidelinesOrRules(entry.getGuidelinesOrRules());
      response.setTotalMembers(entry.getTotalMembers());

      response.setSpaceLink(entry.getMaskedSpaceLink());
      response.setSpaceLinkUnMasked(entry.getSpaceLink());
      response.setIsActive(entry.isActive());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ChatSpaceVisibility visibility = entry.getSpaceVisibility();
      final ChatSpaceVisibilityInfo visibilityInfo = ChatSpaceVisibilityInfo.of(visibility, translate(visibility.getMessageCode()));
      response.setVisibilityInfo(visibilityInfo);

      final Organizer organizer = Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone());
      response.setOrganizer(organizer);

      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = ChatSpaceRequestToJoinStatusInfo.of();
      response.setRequestToJoinStatusInfo(requestToJoinStatusInfo);

      final JoinStatus joinStatus = JoinStatus.byChatSpaceStatus(entry.isPrivate());
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));
      response.setJoinStatusInfo(joinStatusInfo);

      return response;

    }
    return null;
  }

  /**
   * Converts a {@link ChatSpace} entity to an approved {@link ChatSpaceResponse}.
   *
   * <p>This method creates a {@link ChatSpaceResponse} representation of the given {@link ChatSpace},
   * setting it to reflect an approved status for joining the chat space.</p>
   *
   * @param entry the {@link ChatSpace} entity to convert; must not be {@code null}.
   * @return a {@link ChatSpaceResponse} with approved request-to-join and join status information,
   *         or {@code null} if the input {@code entry} is {@code null}.
   */
  @Override
  public ChatSpaceResponse toChatSpaceResponseByAdminUpdate(final ChatSpace entry) {
    if (nonNull(entry)) {
      final ChatSpaceResponse chatSpaceResponse = toChatSpaceResponse(entry);
      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final ChatSpaceRequestToJoinStatus requestToJoinStatus = ChatSpaceRequestToJoinStatus.approved();

      setMembershipInfo(chatSpaceResponse, requestToJoinStatus, joinStatus, true, true);
      return chatSpaceResponse;
    }
    return null;
  }

  /**
   * Converts a list of {@code ChatSpace} entities to a list of {@code ChatSpaceResponse} objects.
   *
   * <p>This method checks if the provided list of {@code ChatSpace} entries is non-null.
   * It filters out any null entries, maps each valid {@code ChatSpace} entity to a
   * {@code ChatSpaceResponse} object, and returns the resulting list. If the input list
   * is null, the method returns an empty list.</p>
   *
   * @param entries the list of {@code ChatSpace} entities to convert
   * @return a list of {@code ChatSpaceResponse} objects with the mapped data, or an empty list if the input is null
   **/
  @Override
  public List<ChatSpaceResponse> toChatSpaceResponses(final List<ChatSpace> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toChatSpaceResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Sets the membership information for a given chat space.
   *
   * <p>This method populates the membership-related fields of the chat space
   * by constructing various membership status objects, including join status,
   * request to join status, member info, admin info, and membership activity info.</p>
   *
   * <p>If the `chatSpace` object is not null, it creates the following objects:
   * {@link JoinStatusInfo}, {@link ChatSpaceRequestToJoinStatusInfo},
   * {@link IsAChatSpaceMemberInfo}, {@link IsAChatSpaceAdminInfo},
   * {@link IsChatSpaceMemberLeftInfo}, and {@link IsChatSpaceMemberRemovedInfo}.
   * These objects are then combined into a {@link ChatSpaceMembershipInfo} object
   * which is set in the chat space.</p>
   *
   * @param chatSpace the chat space object to update with membership info
   * @param requestToJoinStatus the status of the user's request to join the chat space
   * @param joinStatus the current join status of the user in the chat space
   * @param isAMember whether the user is currently a member of the chat space
   * @param isAdmin whether the user is an admin of the chat space
   */
  @Override
  public void setMembershipInfo(
      final ChatSpaceResponse chatSpace,
      final ChatSpaceRequestToJoinStatus requestToJoinStatus,
      final JoinStatus joinStatus,
      final boolean isAMember,
      final boolean isAdmin) {
    if (nonNull(chatSpace)) {
      final JoinStatusInfo joinStatusInfo = toJoinStatusInfo(chatSpace, joinStatus);
      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = toRequestToJoinStatusInfo(chatSpace, requestToJoinStatus);
      final IsAChatSpaceMemberInfo isAChatSpaceMemberInfo = toIsAChatSpaceMemberInfo(isAMember);
      final IsAChatSpaceAdminInfo isAChatSpaceAdminInfo = toIsAChatSpaceAdminInfo(isAdmin);
      final IsChatSpaceMemberLeftInfo isChatSpaceMemberLeftInfo = toIsChatSpaceMemberLeftInfo(isAMember);
      final IsChatSpaceMemberRemovedInfo isChatSpaceMemberRemovedInfo = toIsChatSpaceMemberRemovedInfo(isAMember);

      final ChatSpaceMembershipInfo chatSpaceMembershipInfo = ChatSpaceMembershipInfo.of(
        requestToJoinStatusInfo,
        joinStatusInfo,
        isAChatSpaceMemberInfo,
        isAChatSpaceAdminInfo,
        isChatSpaceMemberLeftInfo,
        isChatSpaceMemberRemovedInfo);
      chatSpace.setMembershipInfo(chatSpaceMembershipInfo);
    }
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
  private ChatSpaceRequestToJoinStatusInfo toRequestToJoinStatusInfo(final ChatSpaceResponse chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
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
  private JoinStatusInfo toJoinStatusInfo(final ChatSpaceResponse chatSpace, final JoinStatus joinStatus) {
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
  public IsChatSpaceMemberLeftInfo toIsChatSpaceMemberLeftInfo(final boolean hasLeft) {
    final IsChatSpaceMemberLeft isChatSpaceMemberLeft = IsChatSpaceMemberLeft.by(hasLeft);
    return IsChatSpaceMemberLeftInfo.of(hasLeft, translate(isChatSpaceMemberLeft.getMessageCode()));
  }

}
