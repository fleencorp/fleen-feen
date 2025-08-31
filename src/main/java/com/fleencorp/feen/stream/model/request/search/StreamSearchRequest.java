package com.fleencorp.feen.stream.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.stream.constant.core.StreamType;
import com.fleencorp.feen.stream.constant.core.StreamVisibility;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamSearchRequest extends SearchRequest {

  @JsonProperty("title")
  protected String title;

  @JsonProperty("timezone")
  protected String timezone;

  @JsonProperty("single_events")
  protected Boolean singleEvents;

  @JsonProperty("show_deleted")
  protected Boolean showDeleted;

  @JsonProperty("order_by")
  protected String orderBy;

  @JsonProperty("stream_visibility")
  @ToUpperCase
  protected String streamVisibility;

  @JsonProperty("stream_type")
  @ToUpperCase
  protected String streamType;

  @JsonProperty("another_user_id")
  protected Long anotherUserId;

  public StreamVisibility getVisibility(final StreamVisibility defaultVisibility) {
    final StreamVisibility actualStreamVisibility = StreamVisibility.of(streamVisibility);
    return nonNull(actualStreamVisibility) ? actualStreamVisibility : defaultVisibility;
  }

  public StreamType getStreamType() {
    return StreamType.of(streamType);
  }

  public void setDefaultStreamType() {
    if (isNull(streamType)) {
      streamType = StreamType.event();
    }
  }

  public boolean hasAnotherUser() {
    return nonNull(anotherUserId);
  }

  public Member getAnotherUser() {
    return hasAnotherUser() ? Member.of(anotherUserId) : null;
  }
}
