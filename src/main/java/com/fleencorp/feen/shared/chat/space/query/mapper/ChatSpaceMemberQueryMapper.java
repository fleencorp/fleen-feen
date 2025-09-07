package com.fleencorp.feen.shared.chat.space.query.mapper;

import com.fleencorp.feen.shared.chat.space.model.ChatSpaceMemberData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ChatSpaceMemberQueryMapper implements RowMapper<ChatSpaceMemberData> {

  private ChatSpaceMemberQueryMapper() {}

  public static ChatSpaceMemberQueryMapper of() {
    return new ChatSpaceMemberQueryMapper();
  }

  @Override
  public ChatSpaceMemberData mapRow(ResultSet rs, int rowNum) throws SQLException {
    final ChatSpaceMemberData chatSpaceMember = new ChatSpaceMemberData();

    Long chatSpaceMemberId = rs.getLong("chatSpaceMemberId");
    String parentExternalIdOrName = rs.getString("parentExternalIdOrName");
    String externalIdOrName = rs.getString("externalIdOrName");
    Long chatSpaceId = rs.getLong("chatSpaceId");
    Long memberId = rs.getLong("memberId");

    Boolean left = rs.getBoolean("hasLeft");
    Boolean removed = rs.getBoolean("removed");

    String memberComment = rs.getString("memberComment");
    String spaceAdminComment = rs.getString("spaceAdminComment");
    String emailAddress = rs.getString("emailAddress");
    String fullName = rs.getString("fullName");
    String username = rs.getString("username");
    String profilePhoto = rs.getString("profilePhoto");

    chatSpaceMember.setChatSpaceMemberId(chatSpaceMemberId);
    chatSpaceMember.setParentExternalIdOrName(parentExternalIdOrName);
    chatSpaceMember.setExternalIdOrName(externalIdOrName);
    chatSpaceMember.setChatSpaceId(chatSpaceId);
    chatSpaceMember.setMemberId(memberId);

    chatSpaceMember.setLeft(left);
    chatSpaceMember.setRemoved(removed);

    chatSpaceMember.setMemberComment(memberComment);
    chatSpaceMember.setSpaceAdminComment(spaceAdminComment);
    chatSpaceMember.setEmailAddress(emailAddress);
    chatSpaceMember.setFullName(fullName);
    chatSpaceMember.setUsername(username);
    chatSpaceMember.setProfilePhoto(profilePhoto);

    return chatSpaceMember;
  }
}

