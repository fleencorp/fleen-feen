package com.fleencorp.feen.model.search.stream.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.model.info.stream.StreamTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Supplier;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "stream_type",
  "result"
})
public class EmptyStreamSearchResult extends StreamSearchResult {

  public EmptyStreamSearchResult(final SearchResultView result, final StreamTypeInfo streamTypeInfo) {
    super(result, streamTypeInfo);
  }

  @Override
  public String getMessageCode() {
    return StreamType.isEvent(getStreamType()) ? "empty.event.search" : "empty.live.broadcast.search";
  }

  public static Supplier<StreamSearchResult> of(final SearchResultView result, final StreamTypeInfo streamTypeInfo) {
    return () -> new EmptyStreamSearchResult(result, streamTypeInfo);
  }
}