package com.fleencorp.feen.model.response.stream.base;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStreamResponse extends CreateStreamResponse {

  public UpdateStreamResponse(final Long streamId, final StreamTypeInfo streamTypeInfo, final StreamResponse stream) {
    super(streamId, streamTypeInfo, stream);
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "update.event" : "update.live.broadcast";
  }

  public static UpdateStreamResponse of(final Long streamId, final StreamTypeInfo streamTypeInfo, final StreamResponse stream) {
    return new UpdateStreamResponse(streamId, streamTypeInfo, stream);
  }
}
