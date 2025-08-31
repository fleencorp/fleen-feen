package com.fleencorp.feen.link.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.link.constant.LinkParentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkSearchRequest extends SearchRequest {

  @ToUpperCase
  @JsonProperty("parent_type")
  protected String parentType;

  @NotNull
  @IsNumber
  @JsonProperty("parent_id")
  protected String parentId;

  public Long getParentId() {
    return nonNull(parentId) ? Long.valueOf(parentId) : null;
  }

  public Long getChatSpaceId() {
    return Long.valueOf(parentId);
  }

  public LinkParentType getParentType() {
    return LinkParentType.of(parentType);
  }

  public static LinkSearchRequest of(final Long chatSpaceId) {
    final String parentId = String.valueOf(chatSpaceId);

    final LinkSearchRequest linkSearchRequest = new LinkSearchRequest();
    linkSearchRequest.setParentId(parentId);
    linkSearchRequest.setParentType(LinkParentType.CHAT_SPACE.toString());

    return linkSearchRequest;
  }

}