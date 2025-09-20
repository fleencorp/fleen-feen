package com.fleencorp.feen.bookmark.service;

import com.fleencorp.feen.bookmark.model.dto.BookmarkDto;
import com.fleencorp.feen.bookmark.model.response.BookmarkCreateResponse;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;

public interface BookmarkService {

  BookmarkCreateResponse bookmark(BookmarkDto bookmarkDto, RegisteredUser user)
    throws ChatSpaceNotFoundException, PollNotFoundException, SoftAskNotFoundException,
      SoftAskReplyNotFoundException, StreamNotFoundException, FailedOperationException;
}
