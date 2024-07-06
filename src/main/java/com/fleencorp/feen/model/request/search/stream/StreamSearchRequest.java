package com.fleencorp.feen.model.request.search.stream;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;
import static java.util.Objects.nonNull;

@SuperBuilder
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
  protected String streamVisibility;

  @JsonProperty("another_user_id")
  protected Long anotherUserId;

  public StreamVisibility getVisibility(StreamVisibility defaultVisibility) {
    StreamVisibility actualStreamVisibility = parseEnumOrNull(streamVisibility, StreamVisibility.class);
    if (nonNull(actualStreamVisibility)) {
      return actualStreamVisibility;
    }
    return defaultVisibility;
  }
}
