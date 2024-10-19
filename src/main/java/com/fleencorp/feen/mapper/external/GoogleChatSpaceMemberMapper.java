package com.fleencorp.feen.mapper.external;

import com.fleencorp.feen.model.response.external.google.chat.membership.base.GoogleChatSpaceMemberResponse;
import com.fleencorp.feen.util.external.google.GoogleApiUtil;
import com.google.chat.v1.Membership;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.convertToLocalDateTime;
import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceMemberIdOrNameFrom;

/**
 * A mapper class for converting {@link Membership} objects to {@link GoogleChatSpaceMemberResponse} objects.
 *
 * <p>This class provides static methods to facilitate the transformation of data between the
 * Google Chat API model and application-specific response models. The mapping methods
 * ensure that the data is correctly formatted and that any necessary conversions are applied.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public class GoogleChatSpaceMemberMapper {

  private GoogleChatSpaceMemberMapper() {}

  /**
   * Converts a {@link Membership} object into a {@link GoogleChatSpaceMemberResponse} object.
   *
   * <p>This method takes a {@link Membership} instance and maps its properties to create a
   * corresponding {@link GoogleChatSpaceMemberResponse}. The name is extracted using the
   * {@link GoogleApiUtil#getSpaceMemberIdOrNameFrom(String)} method, while the role and state are
   * converted to their string representations. The creation time is transformed into a
   * local date-time format using the {@link GoogleApiUtil#convertToLocalDateTime(com.google.protobuf.Timestamp)} method.</p>
   *
   * @param membership the {@link Membership} object to be converted into a
   *                   {@link GoogleChatSpaceMemberResponse}.
   * @return a {@link GoogleChatSpaceMemberResponse} object populated with the member's
   *         name, role, state, and creation time.
   */
  public static GoogleChatSpaceMemberResponse toGoogleChatSpaceMemberResponse(final Membership membership) {
    return GoogleChatSpaceMemberResponse.builder()
      .name(getSpaceMemberIdOrNameFrom(membership.getName()))
      .role(membership.getRole().toString())
      .state(membership.getState().toString())
      .createTime(convertToLocalDateTime(membership.getCreateTime()))
      .build();
  }
}
