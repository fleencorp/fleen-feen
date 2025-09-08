package com.fleencorp.feen.shared.stream.query.mapper.stream;

import com.fleencorp.feen.shared.stream.model.StreamData;
import com.fleencorp.feen.stream.constant.core.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StreamQueryMapper implements RowMapper<StreamData> {

  private StreamQueryMapper() {}

  public static StreamQueryMapper of() {
    return new StreamQueryMapper();
  }

  @Override
  public StreamData mapRow(ResultSet rs, int rowNum) throws SQLException {
    StreamData stream = new StreamData();

    stream.setStreamId(rs.getLong("streamId"));
    stream.setExternalId(rs.getString("externalId"));
    stream.setChatSpaceId(rs.getLong("chatSpaceId"));
    stream.setTitle(rs.getString("title"));
    stream.setDescription(rs.getString("description"));
    stream.setTags(rs.getString("tags"));
    stream.setLocation(rs.getString("location"));

    stream.setTotalSpeakers(rs.getInt("totalSpeakers"));
    stream.setTotalAttendees(rs.getInt("totalAttendees"));
    stream.setBookmarkCount(rs.getInt("bookmarkCount"));
    stream.setLikeCount(rs.getInt("likeCount"));
    stream.setShareCount(rs.getInt("shareCount"));

    stream.setTimezone(rs.getString("timezone"));
    stream.setScheduledStartDate(rs.getTimestamp("scheduledStartDate").toLocalDateTime());
    stream.setScheduledEndDate(rs.getTimestamp("scheduledEndDate").toLocalDateTime());

    stream.setStreamLink(rs.getString("streamLink"));
    stream.setThumbnailLink(rs.getString("thumbnailLink"));
    stream.setOtherDetails(rs.getString("otherDetails"));
    stream.setOtherLink(rs.getString("otherLink"));
    stream.setGroupOrOrganizationName(rs.getString("groupOrOrganizationName"));
    stream.setMusicLink(rs.getString("musicLink"));

    final String streamSourceValue = rs.getString("streamSource");
    final String streamTypeValue = rs.getString("streamType");
    final String streamCreationTypeValue = rs.getString("streamCreationType");
    final String streamVisibilityValue = rs.getString("streamVisibility");
    final String streamStatusValue = rs.getString("streamStatus");

    final StreamSource streamSource = StreamSource.of(streamSourceValue);
    final StreamType streamType = StreamType.of(streamTypeValue);
    final StreamCreationType streamCreationType = StreamCreationType.of(streamCreationTypeValue);
    final StreamVisibility streamVisibility = StreamVisibility.of(streamVisibilityValue);
    final StreamStatus streamStatus = StreamStatus.of(streamStatusValue);

    stream.setStreamSource(streamSource);
    stream.setStreamType(streamType);
    stream.setStreamCreationType(streamCreationType);
    stream.setStreamVisibility(streamVisibility);
    stream.setStreamStatus(streamStatus);

    stream.setOrganizerName(rs.getString("organizerName"));
    stream.setOrganizerEmail(rs.getString("organizerEmail"));
    stream.setOrganizerPhone(rs.getString("organizerPhone"));
    stream.setMemberId(rs.getLong("memberId"));

    stream.setDeleted(rs.getBoolean("deleted"));
    stream.setForKids(rs.getBoolean("forKids"));
    stream.setSlug(rs.getString("slug"));

    stream.setCreatedOn(rs.getTimestamp("createdOn").toLocalDateTime());
    stream.setUpdatedOn(rs.getTimestamp("updatedOn").toLocalDateTime());

    return stream;
  }
}

