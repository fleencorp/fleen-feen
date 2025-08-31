package com.fleencorp.feen.poll.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
public class PollSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("author_id")
  private String authorId;

  @ToUpperCase
  @JsonProperty("parent_type")
  protected String pollParentType;

  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Member author;

  public PollParentType getPollParentType() {
    return PollParentType.of(pollParentType);
  }

  public Long getParentId() {
    return nonNull(parentId) ? Long.parseLong(parentId) : 0L;
  }

  public boolean isParentValid() {
    return getParentId() > 1;
  }

  public boolean isChatSpacePollSearchRequest() {
    return isParentValid() && PollParentType.isChatSpace(getPollParentType());
  }

  public boolean isStreamPollSearchRequest() {
    return isParentValid() && PollParentType.isStream(getPollParentType());
  }

  public boolean isByAuthor() {
    return nonNull(author);
  }

  public boolean hasAuthor() {
    return nonNull(author);
  }

  public Long getAuthorId() {
    return hasAuthor() ? author.getMemberId() : Long.parseLong(authorId);
  }
}
