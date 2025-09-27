package com.fleencorp.feen.softask.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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

  @DecimalMin(value = "-90.0", message = "{user.location.latitude.DecimalMin}")
  @DecimalMax(value = "90.0", message = "{user.location.latitude.DecimalMax}")
  protected Double latitude;

  @DecimalMin(value = "-180.0", message = "{user.location.longitude.DecimalMin}")
  @DecimalMax(value = "180.0", message = "{user.location.longitude.DecimalMax}")
  protected Double longitude;

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

  public boolean hasLatitudeAndLongitude() {
    return nonNull(latitude) && nonNull(longitude);
  }

  public Long getAuthorId() {
    return hasAuthor() ? author.getMemberId() : null;
  }

  public UserOtherDetailHolder getUserOtherDetail() {
    if (hasLatitudeAndLongitude()) {
      return UserOtherDetailHolder.of(latitude, longitude);
    }

    return UserOtherDetailHolder.empty();
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
