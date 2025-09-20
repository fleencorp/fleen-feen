package com.fleencorp.feen.shared.shared.count.service.impl;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.shared.count.constant.ShareCountParentType;
import com.fleencorp.feen.shared.shared.count.model.dto.ShareDto;
import com.fleencorp.feen.shared.shared.count.model.response.ShareResponse;
import com.fleencorp.feen.shared.shared.count.repository.ShareCountRepository;
import com.fleencorp.feen.shared.shared.count.service.ShareCountService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShareCountServiceImpl implements ShareCountService {

  private final ShareCountRepository shareCountRepository;
  private final Localizer localizer;

  public ShareCountServiceImpl(
      final ShareCountRepository shareCountRepository,
      final Localizer localizer) {
    this.shareCountRepository = shareCountRepository;
    this.localizer = localizer;
  }

  /**
   * Increments the share count for the specified parent entity type based on the
   * provided share request. The parent ID and type are validated before performing
   * the increment. Depending on the type of parent, the corresponding share count
   * is updated in the repository. A localized {@link ShareResponse} is then created
   * and returned to confirm the operation.
   *
   * @param shareDto the request object containing the parent ID and the type of
   *                 entity whose share count should be incremented
   * @return a localized response confirming that the share operation was processed
   * @throws FailedOperationException if the parent ID or parent type is missing
   */
  @Override
  @Transactional
  public ShareResponse share(ShareDto shareDto) {
    Long parentId = shareDto.getParentId();
    ShareCountParentType shareCountParentType = shareDto.getShareCountParentType();
    
    checkIsNullAny(List.of(parentId, shareCountParentType), FailedOperationException::new);

    switch (shareCountParentType) {
      case CHAT_SPACE -> incrementChatSpaceShareCount(parentId);
      case POLL ->  incrementPollShareCount(parentId);
      case SOFT_ASK -> incrementSoftAskShareCount(parentId);
      case SOFT_ASK_REPLY -> incrementSoftAskReplyShareCount(parentId);
      case STREAM -> incrementStreamShareCount(parentId);
    }

    ShareResponse shareResponse = ShareResponse.of();
    return localizer.of(shareResponse);
  }

  private void incrementChatSpaceShareCount(Long id) {
    shareCountRepository.incrementChatSpaceShareCount(id);
  }

  private void incrementPollShareCount(Long id) {
    shareCountRepository.incrementPollShareCount(id);
  }

  private void incrementSoftAskShareCount(Long id) {
    shareCountRepository.incrementSoftAskShareCount(id);
  }

  private void incrementSoftAskReplyShareCount(Long id) {
    shareCountRepository.incrementSoftAskReplyShareCount(id);
  }

  private void incrementStreamShareCount(Long id) {
    shareCountRepository.incrementStreamShareCount(id);
  }

}
