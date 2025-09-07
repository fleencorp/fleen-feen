package com.fleencorp.feen.shared.chat.space.query.mapper;

import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceVisibility;
import com.fleencorp.feen.shared.chat.space.model.ChatSpaceData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ChatSpaceQueryMapper implements RowMapper<ChatSpaceData> {

  private ChatSpaceQueryMapper() {}

  public static ChatSpaceQueryMapper of() {
    return new ChatSpaceQueryMapper();
  }

  @Override
  public ChatSpaceData mapRow(ResultSet rs, int rowNum) throws SQLException {
    final ChatSpaceData chatSpace = new ChatSpaceData();

    Long chatSpaceId = rs.getLong("chatSpaceId");
    String externalIdOrName = rs.getString("externalIdOrName");
    String title = rs.getString("title");
    String description = rs.getString("description");
    String tags = rs.getString("tags");
    String guidelinesOrRules = rs.getString("guidelinesOrRules");
    String spaceLink = rs.getString("spaceLink");

    Long organizerId = rs.getLong("organizerId");
    String organizerName = rs.getString("organizerName");

    String spaceVisibilityValue = rs.getString("spaceVisibility");
    String statusValue = rs.getString("status");

    Integer totalMembers = rs.getInt("totalMembers");
    Boolean deleted = rs.getBoolean("deleted");

    Integer likeCount = rs.getInt("likeCount");
    Integer bookmarkCount = rs.getInt("bookmarkCount");
    Integer shareCount = rs.getInt("shareCount");
    String slug = rs.getString("slug");

    ChatSpaceVisibility spaceVisibility = ChatSpaceVisibility.valueOf(spaceVisibilityValue);
    ChatSpaceStatus status = ChatSpaceStatus.valueOf(statusValue);

    chatSpace.setChatSpaceId(chatSpaceId);
    chatSpace.setExternalIdOrName(externalIdOrName);
    chatSpace.setTitle(title);
    chatSpace.setDescription(description);
    chatSpace.setTags(tags);
    chatSpace.setGuidelinesOrRules(guidelinesOrRules);
    chatSpace.setSpaceLink(spaceLink);

    chatSpace.setOrganizerId(organizerId);
    chatSpace.setOrganizerName(organizerName);

    chatSpace.setSpaceVisibility(spaceVisibility);
    chatSpace.setStatus(status);

    chatSpace.setTotalMembers(totalMembers);
    chatSpace.setDeleted(deleted);

    chatSpace.setLikeCount(likeCount);
    chatSpace.setBookmarkCount(bookmarkCount);
    chatSpace.setShareCount(shareCount);
    chatSpace.setSlug(slug);

    return chatSpace;
  }
}

