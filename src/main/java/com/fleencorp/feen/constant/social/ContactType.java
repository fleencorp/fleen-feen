package com.fleencorp.feen.constant.social;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
 * Enum representing the types of contact share requests.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Getter
public enum ContactType implements ApiParameter {

  EMAIL("Email"),
  FACEBOOK("Facebook"),
  INSTAGRAM("Instagram"),
  LINKEDIN("LinkedIn"),
  PHONE_NUMBER("Phone Number"),
  SNAPCHAT("Snapchat"),
  TELEGRAM("Telegram"),
  TIKTOK("Tiktok"),
  TWITTER_OR_X("Twitter or X"),
  WECHAT("We Chat"),
  WHATSAPP("Whatsapp");

  private final String value;

  ContactType(final String value) {
    this.value = value;
  }

  public static ContactType of(final String value) {
    return parseEnumOrNull(value, ContactType.class);
  }
}
