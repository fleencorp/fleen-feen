package com.fleencorp.feen.shared.stream.query.mapper.stream;

import com.fleencorp.feen.shared.stream.model.StreamData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StreamQueryMapper2 implements RowMapper<StreamData> {

  private StreamQueryMapper2() {}

  public static StreamQueryMapper2 of() {
    return new StreamQueryMapper2();
  }

  @Override
  public StreamData mapRow(ResultSet rs, int rowNum) throws SQLException {
    StreamData stream = new StreamData();

    stream.setStreamId(rs.getLong("streamId"));
    stream.setTitle(rs.getString("title"));
    stream.setExternalId(rs.getString("externalSpaceIdOrName"));

    return stream;
  }
}

