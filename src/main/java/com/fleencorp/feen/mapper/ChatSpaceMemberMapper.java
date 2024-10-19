package com.fleencorp.feen.mapper;

import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import com.fleencorp.feen.model.response.chat.space.member.base.ChatSpaceMemberResponse;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link ChatSpaceMember} entities to their corresponding response DTOs, such as
 * {@link ChatSpaceMemberResponse}.
 *
 * <p>This class contains static methods to convert individual or lists of {@code ChatSpaceMember} objects to
 * their respective {@code ChatSpaceMemberResponse} representations.</p>
 *
 * <p>It is designed to handle null checks and filtering out invalid entries during the mapping process.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class ChatSpaceMemberMapper {

  private ChatSpaceMemberMapper() {}

  /**
   * Converts a {@link ChatSpaceMember} entry to a {@link ChatSpaceMemberResponse}.
   *
   * <p>This method takes a {@code ChatSpaceMember} object as input and maps its relevant fields to
   * a {@code ChatSpaceMemberResponse}. If the input entry is null, the method returns null.</p>
   *
   * @param entry the {@code ChatSpaceMember} object to be converted; can be {@code null}
   * @return a {@code ChatSpaceMemberResponse} built from the given {@code ChatSpaceMember}, or {@code null} if the entry is null
   */
  public static ChatSpaceMemberResponse toChatSpaceMemberResponse(final ChatSpaceMember entry) {
    if (nonNull(entry)) {
      return ChatSpaceMemberResponse.builder()
        .memberId(entry.getMemberId())
        .chatSpaceMemberId(entry.getChatSpaceMemberId())
        .memberName(entry.getFullName())
        .chatSpaceMemberRole(entry.getRole())
        .requestToJoinStatus(entry.getRequestToJoinStatus())
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
  public static List<ChatSpaceMemberResponse> toChatSpaceMemberResponses(final List<ChatSpaceMember> entries) {
    if (nonNull(entries)) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(ChatSpaceMemberMapper::toChatSpaceMemberResponse)
        .toList();
    }
    return List.of();
  }
}
