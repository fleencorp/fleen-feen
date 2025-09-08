package com.fleencorp.feen.shared.stream.query.mapper.stream;

import com.fleencorp.feen.shared.stream.model.StreamData;
import com.fleencorp.feen.stream.constant.core.*;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StreamQueryMapper4 implements RowMapper<StreamData> {

  private StreamQueryMapper4() {}

  public static StreamQueryMapper4 of() {
    return new StreamQueryMapper4();
  }

  @Override
  public StreamData mapRow(ResultSet rs, int rowNum) throws SQLException {
    StreamData stream = StreamData.empty();

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

    stream.setDeleted(rs.getBoolean("deleted"));
    stream.setForKids(rs.getBoolean("forKids"));

    stream.setOrganizerName(rs.getString("organizerName"));
    stream.setOrganizerEmail(rs.getString("organizerEmail"));
    stream.setOrganizerPhone(rs.getString("organizerPhone"));

    stream.setMemberId(rs.getLong("memberId"));

    stream.setCreatedOn(rs.getTimestamp("createdOn").toLocalDateTime());
    stream.setUpdatedOn(rs.getTimestamp("updatedOn").toLocalDateTime());

    stream.setSlug(rs.getString("slug"));
    stream.setExternalSpaceIdOrName(rs.getString("externalSpaceIdOrName"));

    return stream;
  }
}

