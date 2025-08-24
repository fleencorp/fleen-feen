package com.fleencorp.feen.link.constant;

import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

@Getter
public enum LinkType {

  EMAIL(
    "Email",
    "john@example.com",
    "contact@yourbusiness.com"
  ),

  DISCORD(
    "Discord",
      "https://discord.gg/your-invite-code",
      "https://discord.gg/your-business-invite"
  ),

  FACEBOOK(
    "Facebook",
      "https://www.facebook.com/groups/your-group-id",
      "https://www.facebook.com/yourbusinesspage"
  ),

  INSTAGRAM(
    "Instagram",
      "https://www.instagram.com/groups/your-group-id",
      "https://www.instagram.com/yourbusiness"
  ),

  LINKEDIN(
    "LinkedIn",
      "https://www.linkedin.com/groups/your-group-id",
      "https://www.linkedin.com/company/yourbusiness"
  ),

  PHONE_NUMBER(
    "Phone Number",
      "+12392020",
      "+18001234567"
  ),

  SLACK(
    "Slack",
      "https://yourworkspace.slack.com/join/shared_invite/your-invite-code",
      "https://yourbusiness.slack.com"
  ),

  SNAPCHAT(
    "Snapchat",
      "https://snapchat.com/add/your-group",
      "https://snapchat.com/add/yourbusiness"
  ),

  TELEGRAM(
    "Telegram",
      "https://t.me/your_channel_or_group",
      "https://t.me/yourbusiness"
  ),

  TWITTER_OR_X(
    "Twitter or X",
      "https://twitter.com",
      "https://twitter.com/yourbusiness"
  ),

  WHATSAPP(
    "Whatsapp",
      "https://chat.whatsapp.com/your-invite-code",
      "https://wa.me/18001234567"
  ),

  OTHER(
    "Other",
      "https://example.com/your-group-link",
      "https://yourbusiness.com"
  );

  private final String value;
  private final String communityFormat;
  private final String businessFormat;

  LinkType(
      final String value,
      final String communityFormat,
      final String businessFormat) {
    this.value = value;
    this.communityFormat = communityFormat;
    this.businessFormat = businessFormat;
  }

  public static LinkType of(final String value) {
    return parseEnumOrNull(value, LinkType.class);
  }
}

