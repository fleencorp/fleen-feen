package com.fleencorp.feen.mapper.impl.chat.member;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceRequestToJoinStatusInfo;
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
   * Converts a {@link ChatSpaceMember} entry to a {@link ChatSpaceMemberResponse}.
   *
   * <p>This method takes a {@code ChatSpaceMember} object as input and maps its relevant fields to
   * a {@code ChatSpaceMemberResponse}. If the input entry is null, the method returns null.</p>
   *
   * @param entry the {@code ChatSpaceMember} object to be converted; can be {@code null}
   * @return a {@code ChatSpaceMemberResponse} built from the given {@code ChatSpaceMember}, or {@code null} if the entry is null
   */
  @Override
  public ChatSpaceMemberResponse toChatSpaceMemberResponse(final ChatSpaceMember entry, final ChatSpace chatSpace) {
    if (nonNull(entry)) {
      final ChatSpaceRequestToJoinStatus requestToJoinStatus = entry.getRequestToJoinStatus();
      final JoinStatus joinStatus = JoinStatus.getJoinStatus(entry.getRequestToJoinStatus(), chatSpace.getSpaceVisibility());
      final ChatSpaceMemberRole role = entry.getRole();

      return ChatSpaceMemberResponse.builder()
        .memberId(entry.getMemberId())
        .chatSpaceMemberId(entry.getChatSpaceMemberId())
        .memberName(entry.getFullName())
        .chatSpaceMemberRoleInfo(ChatSpaceMemberRoleInfo.of(role, translate(role.getMessageCode())))
        .requestToJoinStatusInfo(ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())))
        .joinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())))
        .build();
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
   * Converts a ChatSpaceMember object to a ChatSpaceMemberRoleInfo object.
   *
   * @param chatSpaceMember the ChatSpaceMember object to convert
   * @return a ChatSpaceMemberRoleInfo object containing the member's role and its translated message
   */
  @Override
  public ChatSpaceMemberRoleInfo toRole(final ChatSpaceMember chatSpaceMember) {
    final ChatSpaceMemberRole role = chatSpaceMember.getRole();
    return ChatSpaceMemberRoleInfo.of(role, translate(role.getMessageCode()));
  }
}
