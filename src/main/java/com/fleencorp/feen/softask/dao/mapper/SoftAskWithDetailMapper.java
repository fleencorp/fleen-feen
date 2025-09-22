package com.fleencorp.feen.softask.dao.mapper;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.core.SoftAskStatus;
import com.fleencorp.feen.softask.constant.core.SoftAskVisibility;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SoftAskWithDetailMapper implements RowMapper<SoftAskWithDetail> {

  @Override
  public SoftAskWithDetail mapRow(ResultSet rs, int rowNum) throws SQLException {

    Long softAskId = rs.getLong("softAskId");
    String title = rs.getString("title");
    String description = rs.getString("description");
    String tags = rs.getString("tags");
    String link = rs.getString("link");
    Long parentId = rs.getLong("parentId");
    String parentTitle = rs.getString("parentTitle");

    String parentTypeValue = rs.getString("parentType");
    String visibilityValue = rs.getString("visibility");
    String statusValue = rs.getString("status");
    String locationVisibilityValue = rs.getString("locationVisibility");
    String moodTagValue = rs.getString("moodTag");

    Long chatSpaceId = rs.getLong("chatSpaceId");
    Long pollId = rs.getLong("pollId");
    Long streamId = rs.getLong("streamId");
    Long authorId = rs.getLong("authorId");

    String geoHash = rs.getString("geohash");
    String geoHashPrefix = rs.getString("geohashPrefix");

    Boolean deleted = rs.getBoolean("deleted");
    Boolean visible = rs.getBoolean("visible");

    Integer bookmarkCount = rs.getInt("bookmarkCount");
    Integer participantCount = rs.getInt("participantCount");
    Integer replyCount = rs.getInt("replyCount");
    Integer shareCount = rs.getInt("shareCount");
    Integer voteCount = rs.getInt("voteCount");

    BigDecimal latitude = rs.getObject("latitude", BigDecimal.class);
    BigDecimal longitude = rs.getObject("longitude", BigDecimal.class);

    String slug = rs.getString("slug");

    LocalDateTime createdOn = rs.getTimestamp("createdOn").toLocalDateTime();
    LocalDateTime updatedOn = rs.getTimestamp("updatedOn").toLocalDateTime();

    SoftAsk softAsk = new SoftAsk();
    softAsk.setSoftAskId(softAskId);
    softAsk.setTitle(title);
    softAsk.setDescription(description);
    softAsk.setTags(tags);
    softAsk.setLink(link);
    softAsk.setParentId(parentId);
    softAsk.setParentTitle(parentTitle);

    SoftAskParentType parentType = SoftAskParentType.of(parentTypeValue);
    SoftAskVisibility visibility = SoftAskVisibility.of(visibilityValue);
    SoftAskStatus status = SoftAskStatus.of(statusValue);
    LocationVisibility locationVisibility = LocationVisibility.of(locationVisibilityValue);
    MoodTag moodTag = MoodTag.of(moodTagValue);

    softAsk.setSoftAskParentType(parentType);
    softAsk.setSoftAskVisibility(visibility);
    softAsk.setSoftAskStatus(status);
    softAsk.setLocationVisibility(locationVisibility);
    softAsk.setMoodTag(moodTag);

    softAsk.setChatSpaceId(chatSpaceId);
    softAsk.setPollId(pollId);
    softAsk.setStreamId(streamId);
    softAsk.setAuthorId(authorId);

    softAsk.setGeoHash(geoHash);
    softAsk.setGeoHashPrefix(geoHashPrefix);

    softAsk.setDeleted(deleted);
    softAsk.setVisible(visible);

    softAsk.setBookmarkCount(bookmarkCount);
    softAsk.setParticipantCount(participantCount);
    softAsk.setReplyCount(replyCount);
    softAsk.setShareCount(shareCount);
    softAsk.setVoteCount(voteCount);

    softAsk.setLatitude(latitude);
    softAsk.setLongitude(longitude);
    softAsk.setSlug(slug);

    softAsk.setCreatedOn(createdOn);
    softAsk.setUpdatedOn(updatedOn);

    Long participantId = rs.getLong("participantId");
    String username = rs.getString("username");
    String displayName = rs.getString("displayName");
    String avatar = rs.getString("avatar");
    Double distance = rs.getObject("distance", Double.class);

    return new SoftAskWithDetail(
      softAsk,
      participantId,
      username,
      displayName,
      avatar,
      distance
    );
  }
}
