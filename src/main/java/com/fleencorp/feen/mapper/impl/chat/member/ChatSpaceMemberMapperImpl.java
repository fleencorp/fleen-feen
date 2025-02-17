package com.fleencorp.feen.mapper.impl.chat.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.chat.space.membership.IsAChatSpaceAdmin;
import com.fleencorp.feen.constant.chat.space.membership.IsAChatSpaceMember;
import com.fleencorp.feen.constant.chat.space.membership.IsChatSpaceMemberLeft;
import com.fleencorp.feen.constant.chat.space.membership.IsChatSpaceMemberRemoved;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.*;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
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
public class ChatSpaceMemberMapperImpl implements ChatSpaceMemberMapper {

  private final MessageSource messageSource;

  /**
   * Constructor for the ChatSpaceMemberMapper class.
   *
   * @param messageSource the MessageSource used for message translation
   */
  public ChatSpaceMemberMapperImpl(
      final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Translates a message code into a localized message based on the current locale.
   *
   * @param messageCode the message code to be translated
   * @return the localized message corresponding to the given message code
   */
  private String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
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
      response.setMemberId(entry.getMemberId());
      response.setChatSpaceMemberId(entry.getChatSpaceMemberId());
      response.setMemberName(entry.getFullName());

      final ChatSpaceMemberRole role = entry.getRole();
      final ChatSpaceMemberRoleInfo memberRoleInfo = ChatSpaceMemberRoleInfo.of(role, translate(role.getMessageCode()));
      response.setChatSpaceMemberRoleInfo(memberRoleInfo);

      final ChatSpaceRequestToJoinStatus requestToJoinStatus = entry.getRequestToJoinStatus();
      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode()));

      final JoinStatus joinStatus = JoinStatus.getJoinStatus(entry.getRequestToJoinStatus(), chatSpace.getSpaceVisibility(), entry.isAMember(), entry.isRemoved());
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()));

      final IsAChatSpaceMember isAChatSpaceMember = IsAChatSpaceMember.by(entry.isAMember());
      final IsAChatSpaceMemberInfo isAChatSpaceMemberInfo = IsAChatSpaceMemberInfo.of(entry.isAMember(), translate(isAChatSpaceMember.getMessageCode()));

      final IsAChatSpaceAdmin isAChatSpaceAdmin = IsAChatSpaceAdmin.by(entry.isAdmin());
      final IsAChatSpaceAdminInfo isAChatSpaceAdminInfo = IsAChatSpaceAdminInfo.of(entry.isAdmin(), translate(isAChatSpaceAdmin.getMessageCode()), translate(isAChatSpaceAdmin.getMessageCode2()));

      final IsChatSpaceMemberRemoved isChatSpaceMemberRemoved = IsChatSpaceMemberRemoved.by(entry.isRemoved());
      final IsChatSpaceMemberRemovedInfo isChatSpaceMemberRemovedInfo = IsChatSpaceMemberRemovedInfo.of(entry.isRemoved(), translate(isChatSpaceMemberRemoved.getMessageCode()));

      final IsChatSpaceMemberLeft isChatSpaceMemberLeft = IsChatSpaceMemberLeft.by(entry.hasLeft());
      final IsChatSpaceMemberLeftInfo isChatSpaceMemberLeftInfo = IsChatSpaceMemberLeftInfo.of(entry.hasLeft(), translate(isChatSpaceMemberLeft.getMessageCode()));

      final ChatSpaceMembershipInfo chatSpaceMembershipInfo = ChatSpaceMembershipInfo.of(
        requestToJoinStatusInfo,
        joinStatusInfo,
        isAChatSpaceMemberInfo,
        isAChatSpaceAdminInfo,
        isChatSpaceMemberLeftInfo,
        isChatSpaceMemberRemovedInfo
      );
      response.setMembershipInfo(chatSpaceMembershipInfo);

      return response;
    }
    return null;
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
   * Converts a ChatSpaceMemberRole object to a ChatSpaceMemberRoleInfo object.
   *
   * @param role the ChatSpaceMemberRole object to convert
   * @return a ChatSpaceMemberRoleInfo object containing the member's role and its translated message
   */
  @Override
  public ChatSpaceMemberRoleInfo toRole(final ChatSpaceMemberRole role) {
    return ChatSpaceMemberRoleInfo.of(role, translate(role.getMessageCode()));
  }
}
