package com.fleencorp.feen.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.link.constant.LinkParentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class LinkSearchRequest extends SearchRequest {

  @ToUpperCase
  @JsonProperty("parent_type")
  protected String parentType;

  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  public Long getChatSpaceId() {
    return Long.valueOf(parentId);
  }

  public LinkParentType getParentType() {
    return LinkParentType.of(parentType);
  }

  public boolean isChatSpaceSearchRequest() {
    return nonNull(parentId) && LinkParentType.isChatSpace(parentType);
  }

  public static LinkSearchRequest of(final Long chatSpaceId) {
    final String parentId = String.valueOf(chatSpaceId);

    final LinkSearchRequest linkSearchRequest = new LinkSearchRequest();
    linkSearchRequest.setParentId(parentId);
    linkSearchRequest.setParentType(LinkParentType.CHAT_SPACE.toString());

    return linkSearchRequest;
  }

}