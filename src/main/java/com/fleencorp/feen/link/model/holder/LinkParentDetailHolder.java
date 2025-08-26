package com.fleencorp.feen.link.model.holder;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.link.constant.LinkParentType;

public record LinkParentDetailHolder(Business business, ChatSpace chatSpace, LinkParentType parentType, boolean isAdmin) {

  public Long parentId() {
    return switch (parentType) {
      case BUSINESS -> business.getBusinessId();
      case CHAT_SPACE -> chatSpace.getChatSpaceId();
      default -> throw FailedOperationException.of();
    };
  }

  public static LinkParentDetailHolder of(final Business business, final ChatSpace chatSpace, final LinkParentType parentType, final boolean isAdmin) {
    return new LinkParentDetailHolder(business, chatSpace, parentType, isAdmin);
  }

  public static LinkParentDetailHolder of(final Business business, final ChatSpace chatSpace, final LinkParentType parentType) {
    return new LinkParentDetailHolder(business, chatSpace, parentType, false);
  }

  public static LinkParentDetailHolder ofBusiness(final Business business) {
    return new LinkParentDetailHolder(business, null, LinkParentType.BUSINESS);
  }

  public static LinkParentDetailHolder ofChatSpace(final ChatSpace chatSpace) {
    return new LinkParentDetailHolder(null, chatSpace, LinkParentType.CHAT_SPACE);
  }
}
