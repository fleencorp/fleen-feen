package com.fleencorp.feen.softask.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftAskSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("parent_id")
  private String parentId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Member author;

  public Long getParentId() {
    return nonNull(parentId) ? Long.parseLong(parentId) : null;
  }

  public boolean isByAuthor() {
    return nonNull(author);
  }

  public boolean hasAuthor() {
    return nonNull(author);
  }

  public Long getAuthorId() {
    return hasAuthor() ? author.getMemberId() : null;
  }

  public static SoftAskSearchRequest of(final Long parentId) {
    final SoftAskSearchRequest searchRequest = new SoftAskSearchRequest();
    searchRequest.setParentId(String.valueOf(parentId));
    return searchRequest;
  }
}
