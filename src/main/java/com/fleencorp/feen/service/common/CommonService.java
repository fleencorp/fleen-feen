package com.fleencorp.feen.service.common;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.security.FleenUser;

import static java.util.Objects.nonNull;

public interface CommonService {

  /**
   * Verifies if the given user is the owner and throws an exception if the user is attempting to perform an action
   * they are not authorized to perform.
   *
   * <p>This method checks if both the {@link Member} owner and the {@link FleenUser} are provided.
   * If the user is the owner (i.e., their user IDs match), a {@link FailedOperationException} is thrown.</p>
   *
   * @param owner the member who is the owner of the resource; must not be null
   * @param user  the user attempting to perform the action; must not be null
   * @throws FailedOperationException if the user is the owner of the resource
   */
  static void verifyIfUserIsAuthorOrCreatorOrOwnerTryingToPerformAction(final Member owner, final FleenUser user) throws FailedOperationException {
    // Check if both the owner and user are provided
    if (nonNull(owner) && nonNull(user)) {
      // Retrieve the ID of the owner of a resource
      final Long ownerUserId = owner.getMemberId();
      // Get the user's ID
      final Long userId = user.getId();
      // If the user is the owner, throw an exception
      if (ownerUserId.equals(userId)) {
        throw new FailedOperationException();
      }
    }
  }
}
