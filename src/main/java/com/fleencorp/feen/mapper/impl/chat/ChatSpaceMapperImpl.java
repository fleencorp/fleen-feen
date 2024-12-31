package com.fleencorp.feen.mapper.impl.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.security.mask.MaskedChatSpaceUri;
import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceVisibilityInfo;
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
   * Converts a {@link ChatSpace} entity to a {@link ChatSpaceResponse}.
   *
   * <p>This method maps the details of the provided {@link ChatSpace} entity into a
   * {@link ChatSpaceResponse}, including visibility, membership information, and other
   * relevant metadata.</p>
   *
   * @param entry the {@link ChatSpace} entity to convert; must not be {@code null}.
   * @return a {@link ChatSpaceResponse} with details of the given {@link ChatSpace}, or
   *         {@code null} if the input {@code entry} is {@code null}.
   */
  @Override
  public ChatSpaceResponse toChatSpaceResponse(final ChatSpace entry) {
    if (nonNull(entry)) {
      final JoinStatus joinStatusNotJoinedPrivate = JoinStatus.notJoinedPrivate();
      final JoinStatus joinStatusNotJoinedPublic = JoinStatus.notJoinedPublic();
      final ChatSpaceVisibility visibility = entry.getSpaceVisibility();

      return ChatSpaceResponse.builder()
        .id(entry.getChatSpaceId())
        .title(entry.getTitle())
        .description(entry.getDescription())
        .tags(entry.getTags())
        .guidelinesOrRules(entry.getGuidelinesOrRules())
        .spaceLink(nonNull(entry.getSpaceLink()) ? MaskedChatSpaceUri.of(entry.getSpaceLink()) : null)
        .spaceLinkUnMasked(entry.getSpaceLink())
        .isActive(entry.getIsActive())
        .visibilityInfo(ChatSpaceVisibilityInfo.of(visibility, translate(visibility.getMessageCode())))
        .totalMembers(entry.getTotalMembers())
        .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
        .requestToJoinStatusInfo(ChatSpaceRequestToJoinStatusInfo.of())
        .joinStatusInfo(entry.isPrivate()
          ? JoinStatusInfo.of(joinStatusNotJoinedPrivate, translate(joinStatusNotJoinedPrivate.getMessageCode()), translate(joinStatusNotJoinedPrivate.getMessageCode2()))
          : JoinStatusInfo.of(joinStatusNotJoinedPublic, translate(joinStatusNotJoinedPublic.getMessageCode()), translate(joinStatusNotJoinedPublic.getMessageCode2())))
        .createdOn(entry.getCreatedOn())
        .updatedOn(entry.getUpdatedOn())
        .build();
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
  public ChatSpaceResponse toChatSpaceResponseApproved(final ChatSpace entry) {
    if (nonNull(entry)) {
      final ChatSpaceResponse chatSpaceResponse = toChatSpaceResponse(entry);
      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final ChatSpaceRequestToJoinStatus requestToJoinStatus = ChatSpaceRequestToJoinStatus.approved();

      chatSpaceResponse.setJoinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())));
      chatSpaceResponse.setRequestToJoinStatusInfo(ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
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
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toChatSpaceResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Updates the {@link ChatSpaceResponse} with request-to-join status and join status information.
   *
   * <p>This method populates the given {@code ChatSpaceResponse} with detailed status information,
   * including the translated messages corresponding to the provided {@link ChatSpaceRequestToJoinStatus}
   * and {@link JoinStatus}.</p>
   *
   * @param chatSpace the {@link ChatSpaceResponse} to be updated; must not be {@code null}.
   * @param requestToJoinStatus the {@link ChatSpaceRequestToJoinStatus} used to derive request-to-join status information;
   *                            may be {@code null}.
   * @param joinStatus the {@link JoinStatus} used to derive join status information; may be {@code null}.
   */
  @Override
  public void update(final ChatSpaceResponse chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus, final JoinStatus joinStatus) {
    if (nonNull(chatSpace)) {
      setRequestToJoinStatusInfo(chatSpace, requestToJoinStatus);
      setJoinStatusInfo(chatSpace, joinStatus);
    }
  }

  /**
   * Sets the request-to-join status information for the provided {@link ChatSpaceResponse}.
   *
   * <p>This method updates the {@code ChatSpaceResponse} object with detailed information
   * about the request-to-join status, including the translated message corresponding
   * to the {@link ChatSpaceRequestToJoinStatus}.</p>
   *
   * @param chatSpace the {@link ChatSpaceResponse} to update; must not be {@code null}.
   * @param requestToJoinStatus the {@link ChatSpaceRequestToJoinStatus} used to derive the status information;
   *                            must not be {@code null}.
   */
  private void setRequestToJoinStatusInfo(final ChatSpaceResponse chatSpace, final ChatSpaceRequestToJoinStatus requestToJoinStatus) {
    if (nonNull(chatSpace) && nonNull(requestToJoinStatus)) {
      chatSpace.setRequestToJoinStatusInfo(ChatSpaceRequestToJoinStatusInfo.of(requestToJoinStatus, translate(requestToJoinStatus.getMessageCode())));
    }
  }

  /**
   * Sets the join status information for the provided {@link ChatSpaceResponse}.
   *
   * <p>This method updates the {@code ChatSpaceResponse} object with detailed join status information,
   * including the translated messages corresponding to the {@link JoinStatus}.</p>
   *
   * @param chatSpace the {@link ChatSpaceResponse} to update; must not be {@code null}.
   * @param joinStatus the {@link JoinStatus} used to derive the join status information; must not be {@code null}.
   */
  private void setJoinStatusInfo(final ChatSpaceResponse chatSpace, final JoinStatus joinStatus) {
    if (nonNull(chatSpace) && nonNull(joinStatus)) {
      chatSpace.setJoinStatusInfo(JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2())));
    }
  }

}
