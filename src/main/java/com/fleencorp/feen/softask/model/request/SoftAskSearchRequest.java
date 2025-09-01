package com.fleencorp.feen.softask.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.common.model.dto.UserOtherDetailDto;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftAskSearchRequest extends SearchRequest {

  @IsNumber
  @JsonProperty("parent_id")
  private String parentId;

  @IsNumber
  @JsonProperty("parent_reply_id")
  private String parentReplyId;

  @JsonProperty("user_other_detail")
  private UserOtherDetailDto userOtherDetailDto;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private IsAMember author;

  public Long getParentId() {
    return nonNull(parentId) ? Long.parseLong(parentId) : null;
  }

  public Long getParentReplyId() {
    return nonNull(parentReplyId) ? Long.parseLong(parentReplyId) : null;
  }

  public boolean isByAuthor() {
    return nonNull(author);
  }

  public boolean hasAuthor() {
    return nonNull(author);
  }

  public boolean hasParentReplyId() {
    return nonNull(parentReplyId);
  }

  public boolean hasParentId() {
    return nonNull(parentId);
  }

  public Long getAuthorId() {
    return hasAuthor() ? author.getMemberId() : null;
  }

  public UserOtherDetailHolder getUserOtherDetail() {
    if (nonNull(userOtherDetailDto) && userOtherDetailDto.hasLatitudeAndLongitude()) {
      return UserOtherDetailHolder.of(userOtherDetailDto.getLatitude(), userOtherDetailDto.getLongitude());
    }

    return UserOtherDetailHolder.empty();
  }

  public Double getLatitude() {
    return nonNull(userOtherDetailDto) ? userOtherDetailDto.getLatitude() : null;
  }

  public Double getLongitude() {
    return nonNull(userOtherDetailDto) ? userOtherDetailDto.getLongitude() : null;
  }

  public void updateParentId(final Long parentId) {
    this.parentId = Objects.toString(parentId, null);
  }

  public static SoftAskSearchRequest of(final Long parentId) {
    final SoftAskSearchRequest searchRequest = new SoftAskSearchRequest();
    searchRequest.setParentId(String.valueOf(parentId));
    return searchRequest;
  }

  public static SoftAskSearchRequest of(final Long parentId, final Long replyId) {
    final SoftAskSearchRequest searchRequest = of(parentId);
    searchRequest.setParentReplyId(String.valueOf(replyId));

    return searchRequest;
  }
}
