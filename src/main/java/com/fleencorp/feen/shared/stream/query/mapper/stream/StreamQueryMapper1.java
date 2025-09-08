package com.fleencorp.feen.shared.stream.query.mapper.stream;

import com.fleencorp.feen.shared.stream.model.StreamData;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class StreamQueryMapper1 implements RowMapper<StreamData> {

  private StreamQueryMapper1() {}

  public static StreamQueryMapper1 of() {
    return new StreamQueryMapper1();
  }

  @Override
  public StreamData mapRow(ResultSet rs, int rowNum) throws SQLException {
    StreamData stream = new StreamData();

    stream.setStreamId(rs.getLong("streamId"));
    stream.setTitle(rs.getString("title"));

    return stream;
  }
}

