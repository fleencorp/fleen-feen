package com.fleencorp.feen.constant.chat.space;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing different types of chat space status.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum ChatSpaceStatus implements ApiParameter {

  ACTIVE(
    "Active", 
    "chat.space.status.active", 
    "chat.space.status.active.2", 
    "chat.space.status.active.3",
    "chat.space.status.active.4",
    "chat.space.status.active.5"),
  
  INACTIVE(
    "Inactive", 
    "chat.space.status.inactive", 
    "chat.space.status.inactive.2", 
    "chat.space.status.inactive.3",
    "chat.space.status.inactive.4",
    "chat.space.status.inactive.5");

  private final String value;
  private final String messageCode;
  private final String messageCode2;
  private final String messageCode3;
  private final String messageCode4;
  private final String messageCode5;

  ChatSpaceStatus(
      final String value,
      final String messageCode,
      final String messageCode2,
      final String messageCode3,
      final String messageCode4,
      final String messageCode5) {
    this.value = value;
    this.messageCode = messageCode;
    this.messageCode2 = messageCode2;
    this.messageCode3 = messageCode3;
    this.messageCode4 = messageCode4;
    this.messageCode5 = messageCode5;
  }

  public static ChatSpaceStatus of(final String value) {
    return parseEnumOrNull(value, ChatSpaceStatus.class);
  }

  public static boolean isActive(final ChatSpaceStatus status) {
    return status == ACTIVE;
  }

  public static boolean isInactive(final ChatSpaceStatus status) {
    return status == INACTIVE;
  }
}
