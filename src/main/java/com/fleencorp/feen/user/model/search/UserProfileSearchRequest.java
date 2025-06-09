package com.fleencorp.feen.user.model.search;

import com.fleencorp.base.model.request.search.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.base.util.FleenUtil.isValidNumber;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileSearchRequest extends SearchRequest {

  private String userId;

  public Long getTargetUserId() {
    return nonNull(userId) && isValidNumber(userId) ? Long.parseLong(userId) : 0;
  }

  public static UserProfileSearchRequest of(final String userId) {
    final UserProfileSearchRequest userProfileSearchRequest = new UserProfileSearchRequest();
    userProfileSearchRequest.setUserId(userId);

    return userProfileSearchRequest;
  }
}
