package com.fleencorp.feen.shared.common.contract;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.contract.SoftAskCommonData;
import com.fleencorp.feen.softask.contract.SoftAskCommonResponse;

import static java.util.Objects.isNull;

public interface IsAuthor {

  void setIsAuthor(boolean isAuthor);

  void markAsAuthor();

  static boolean isAuthor(IsAMember member, Long authorId2) {
    if (isNull(member) || isNull(authorId2)) {
      return false;
    }

    return isAuthor(member.getMemberId(), authorId2);
  }

  static boolean isAuthor(Long authorId1, Long authorId2) {
    if (isNull(authorId1) || isNull(authorId2)) {
      return false;
    }

    return authorId1.equals(authorId2);
  }

  static void setIsAuthorDetails(final SoftAskCommonData entry, final IsAMember member, SoftAskCommonResponse response) {
    if (IsAuthor.isAuthor(member, entry.getAuthorId())) {
      response.markAsAuthor();
    }
  }
}
