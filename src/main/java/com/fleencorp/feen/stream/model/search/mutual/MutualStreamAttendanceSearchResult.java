package com.fleencorp.feen.stream.model.search.mutual;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "result"
})
public class MutualStreamAttendanceSearchResult extends LocalizedResponse {

  @JsonProperty("result")
  protected SearchResult result;

  @JsonIgnore
  private String targetMemberFullName;

  @Override
  public String getMessageCode() {
    return nonNull(result) && result.hasValue() ? "stream.mutual.attended.search" : "stream.mutual.attended.empty.search";
  }

  @Override
  public Object[] getParams() {
    return nonNull(targetMemberFullName)
      ? new Object[] { targetMemberFullName }
      : super.getParams();
  }

  public static MutualStreamAttendanceSearchResult of(final SearchResult result, final String targetMemberFullName) {
    return new MutualStreamAttendanceSearchResult(result, targetMemberFullName);
  }
}