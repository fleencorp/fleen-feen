package com.fleencorp.feen.mapper;

import com.fleencorp.feen.constant.security.mask.MaskedChatSpaceUri;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;

import java.util.List;
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
public class ChatSpaceMapper {

  private ChatSpaceMapper() {}

  /**
   * Converts a {@code ChatSpace} entity to a {@code ChatSpaceResponse} object.
   *
   * <p>This method checks if the provided {@code ChatSpace} entity is non-null. If so, it maps
   * the relevant fields from the entity, such as the chat space ID, title, description, and other
   * details, to a {@code ChatSpaceResponse} object using the builder pattern. If the entry is null,
   * the method returns {@code null}.</p>
   *
   * @param entry the {@code ChatSpace} entity to convert
   * @return a {@code ChatSpaceResponse} object with the mapped data, or {@code null} if the entry is null
   **/
  public static ChatSpaceResponse toChatSpaceResponse(final ChatSpace entry) {
    if (nonNull(entry)) {
      return ChatSpaceResponse.builder()
        .id(entry.getChatSpaceId())
        .title(entry.getTitle())
        .description(entry.getDescription())
        .tags(entry.getTags())
        .guidelinesOrRules(entry.getGuidelinesOrRules())
        .spaceLink(nonNull(entry.getSpaceLink()) ? MaskedChatSpaceUri.of(entry.getSpaceLink()) : null)
        .spaceLinkUnMasked(entry.getSpaceLink())
        .isActive(entry.getIsActive())
        .visibility(entry.getSpaceVisibility())
        .totalMembers(entry.getTotalMembers())
        .organizer(Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone()))
        .createdOn(entry.getCreatedOn())
        .updatedOn(entry.getUpdatedOn())
        .build();
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
  public static List<ChatSpaceResponse> toChatSpaceResponses(final List<ChatSpace> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceMapper::toChatSpaceResponse)
        .toList();
    }
    return List.of();
  }
}
