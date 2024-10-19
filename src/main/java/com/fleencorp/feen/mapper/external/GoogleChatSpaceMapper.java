package com.fleencorp.feen.mapper.external;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;
import com.google.chat.v1.Space;
import lombok.extern.slf4j.Slf4j;

import static com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse.MembershipCount.parseChatSpaceResponse;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.convertToLocalDateTime;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.createSpaceUriFromSpaceName;
import static java.util.Objects.nonNull;

/**
 * Utility class for mapping Google Chat Space objects to response DTOs.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
public class GoogleChatSpaceMapper {

  private GoogleChatSpaceMapper() {}

  /**
   * Converts a Google Chat Space object to a GoogleChatSpaceResponse.
   *
   * <p>This method transforms the provided Space object into a GoogleChatSpaceResponse object
   * by mapping relevant fields and performing any necessary formatting and conversions.</p>
   *
   * @param space The Google Chat Space object to be converted.
   * @return A GoogleChatSpaceResponse object containing the mapped information, or null if the space is null.
   */
  public static GoogleChatSpaceResponse toGoogleChatSpaceResponse(final Space space) {
    // Check if the space object is non-null before proceeding
    if (nonNull(space)) {
      // Parse the space object into a response to retrieve additional details like membership count
      final GoogleChatSpaceResponse response = parseChatSpaceResponse(space.toString());

      // Build and return the GoogleChatSpaceResponse object with mapped properties
      return GoogleChatSpaceResponse.builder()
        .name(space.getName())
        .displayName(space.getDisplayName())
        .description(space.getSpaceDetails().getDescription())
        .guidelinesOrRules(space.getSpaceDetails().getGuidelines())
        .externalId(space.getName())
        .spaceType(space.getSpaceType().toString())
        .spaceHistoryState(space.getSpaceHistoryState().toString())
        .spaceThreadingState(space.getSpaceThreadingState().toString())
        .createTime(convertToLocalDateTime(space.getCreateTime()))
        .spaceUri(createSpaceUriFromSpaceName(space.getName()))
        .membershipCount(response.getMembershipCount())
        .build();
    }
    return null;
  }
}
