package com.fleencorp.feen.contact.constant;

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

  EMAIL("Email", "john@example.com"),
  DISCORD("Discord", "https://discord.gg/your-invite-code"),
  FACEBOOK( "Facebook", "https://www.facebook.com/groups/your-group-id"),
  INSTAGRAM("Instagram", "https://www.instagram.com/groups/your-group-id"),
  LINKEDIN("LinkedIn","https://www.linkedin.com/groups/your-group-id"),
  PHONE_NUMBER("Phone Number", "+12392020"),
  SNAPCHAT("Snapchat", "https://yourworkspace.slack.com/join/shared_invite/your-invite-code"),
  TELEGRAM( "Telegram", "https://t.me/your_channel_or_group"),
  TIKTOK("Tiktok", "https://www.tiktok.com/groups/your-group-id"),
  TWITTER_OR_X("Twitter or X", "https://twitter.com"),
  WHATSAPP("Whatsapp", "https://chat.whatsapp.com/your-invite-code"),
  WECHAT("We Chat", "https://www.wechat.com/groups/your-group-id");

  private final String value;
  private final String format;

  ContactType(
      final String value,
      final String format) {
    this.value = value;
    this.format = format;
  }

  public static ContactType of(final String value) {
    return parseEnumOrNull(value, ContactType.class);
  }
}
