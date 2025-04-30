package com.fleencorp.feen.constant.link;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum LinkType implements ApiParameter {

  EMAIL("Email", "john@example.com"),
  DISCORD("Discord", "https://discord.gg/your-invite-code"),
  FACEBOOK( "Facebook", "https://www.facebook.com/groups/your-group-id"),
  INSTAGRAM("Instagram", "https://www.instagram.com/groups/your-group-id"),
  LINKEDIN("LinkedIn","https://www.linkedin.com/groups/your-group-id"),
  PHONE_NUMBER("Phone Number", "+12392020"),
  SLACK("Slack", "https://yourworkspace.slack.com/join/shared_invite/your-invite-code"),
  SNAPCHAT("Snapchat", "https://yourworkspace.slack.com/join/shared_invite/your-invite-code"),
  TWITTER_OR_X("Twitter or X", "https://twitter.com"),
  TELEGRAM( "Telegram", "https://t.me/your_channel_or_group"),
  WHATSAPP("Whatsapp", "https://chat.whatsapp.com/your-invite-code"),
  OTHER("Other","https://example.com/your-group-link");

  private final String value;
  private final String format;

  LinkType(
      final String value,
      final String format) {
    this.value = value;
    this.format = format;
  }

  public static LinkType of(final String value) {
    return parseEnumOrNull(value, LinkType.class);
  }
}

