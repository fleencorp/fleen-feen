package com.fleencorp.feen.common.constant.mask;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fleencorp.base.constant.base.ApiParameter;
import com.fleencorp.base.serializer.ToStringEnumSerializer;
import com.fleencorp.feen.common.constant.external.google.chat.base.GoogleChatParameter;
import lombok.Getter;
import lombok.Setter;

@Getter
@JsonSerialize(using = ToStringEnumSerializer.class)
public enum MaskedChatSpaceUri implements ApiParameter {

  /**
   * Instance representing a Google Chat Room URL.
   */
  CHAT_SPACE("ChatSpace");

  @Setter
  private String value;

  MaskedChatSpaceUri(final String value) {
    this.value = value;
  }

  /**
   * Returns the string representation of the enum value.
   *
   * @return the string representation of the enum value.
   */
  @Override
  public String toString() {
    return value;
  }

  /**
   * Creates a {@link MaskedChatSpaceUri} instance with a masked chat room URL.
   *
   * <p>This method generates a masked chat room URL using the provided value and
   * returns a {@link MaskedChatSpaceUri} instance with the masked URL.</p>
   *
   * @param value the chat room URL to mask.
   * @return a {@link MaskedChatSpaceUri} instance with the masked chat room URL.
   */
  public static MaskedChatSpaceUri of(final String value) {
    final MaskedChatSpaceUri chatRoomUrl = MaskedChatSpaceUri.CHAT_SPACE;
    chatRoomUrl.setValue(maskChatRoomUrl(value));
    return chatRoomUrl;
  }

  /**
   * Masks a Google Chat Room URL, obscuring parts of the room ID while keeping the base URL visible.
   *
   * <p>This method masks the room ID in the chat room URL, leaving only the first and last characters of the room ID visible.</p>
   *
   * @param url the chat room URL to be masked. It must not be {@code null} and must follow the Google Chat room URL pattern.
   * @return the masked chat room URL with the room ID appropriately obscured.
   * @throws IllegalArgumentException if the {@code url} is {@code null} or doesn't match the expected pattern.
   */
  public static String maskChatRoomUrl(final String url) {
    final String baseUrl = GoogleChatParameter.chatSpaceBaseUri();

    if (url == null || !url.startsWith(baseUrl)) {
      throw new IllegalArgumentException("Invalid chat room URL");
    }

    // Extract the room ID part
    final String roomId = url.substring(baseUrl.length());

    if (roomId.length() < 3) {
      // If the room ID is too short, just return it without masking
      return url;
    }

    // Mask the room ID except for the first and last characters
    final String maskedRoomId = roomId.charAt(0) + "***" + roomId.charAt(roomId.length() - 1);

    // Return the full masked URL
    return baseUrl + maskedRoomId;
  }
}
