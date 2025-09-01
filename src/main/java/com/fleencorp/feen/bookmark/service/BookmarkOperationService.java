package com.fleencorp.feen.bookmark.service;

import com.fleencorp.feen.model.contract.Bookmarkable;
import com.fleencorp.feen.shared.member.contract.IsAMember;

import java.util.Collection;

public interface BookmarkOperationService {

  <T extends Bookmarkable> void populateChatSpaceBookmarksFor(Collection<T> responses, IsAMember member);

  <T extends Bookmarkable> void populateStreamBookmarksFor(Collection<T> responses, IsAMember member);
  
  <T extends Bookmarkable> void populateSoftAskBookmarksFor(Collection<T> responses, IsAMember member);
  
  <T extends Bookmarkable> void populateSoftAskReplyBookmarksFor(Collection<T> responses, IsAMember member);

  <T extends Bookmarkable> void populateBookmarkForReviews(Collection<T> responses, IsAMember member);
}
