package com.fleencorp.feen.shared.shared.count.repository;

public interface ShareCountRepository {

  void incrementSoftAskShareCount(Long id);

  void incrementSoftAskReplyShareCount(Long id);

  void incrementPollShareCount(Long id);

  void incrementStreamShareCount(Long id);

  void incrementChatSpaceShareCount(Long id);

  void incrementBusinessShareCount(Long id);
}
