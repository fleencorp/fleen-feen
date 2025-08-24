package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.repository.softask.SoftAskRepository;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.reply.SoftAskReplySearchService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SoftAskOperationServiceImpl implements SoftAskOperationService {

  private final SoftAskReplySearchService softAskReplySearchService;
  private final SoftAskSearchService softAskSearchService;
  private final SoftAskReplyRepository softAskReplyRepository;
  private final SoftAskRepository softAskRepository;

  public SoftAskOperationServiceImpl(
      final SoftAskReplySearchService softAskReplySearchService,
      final SoftAskSearchService softAskSearchService,
      final SoftAskReplyRepository softAskReplyRepository,
      final SoftAskRepository softAskRepository) {
    this.softAskReplySearchService = softAskReplySearchService;
    this.softAskSearchService = softAskSearchService;
    this.softAskReplyRepository = softAskReplyRepository;
    this.softAskRepository = softAskRepository;
  }


  @Override
  @Transactional
  public SoftAskReply save(final SoftAskReply softAskReply) {
    return softAskReplyRepository.save(softAskReply);
  }

  @Override
  @Transactional
  public SoftAsk save(final SoftAsk softAsk) {
    return softAskRepository.save(softAsk);
  }

  @Override
  public SoftAsk findSoftAsk(final Long softAskId) {
    return softAskSearchService.findSoftAsk(softAskId);
  }

  @Override
  public SoftAskReply findSoftAskReply(final Long softAskId, final Long softAskReplyId) {
    return softAskReplySearchService.findSoftAskReply(softAskId, softAskReplyId);
  }

  private Integer incrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.incrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  private Integer decrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.decrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  @Override
  @Transactional
  public Integer updateVoteCount(final Long softAskId, final boolean isVoted) {
    return isVoted
      ? incrementSoftAskVoteAndGetVoteCount(softAskId)
      : decrementSoftAskVoteAndGetVoteCount(softAskId);
  }

  private Integer incrementSoftAskReplyVoteAndGetVoteCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.incrementVoteCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskId, softAskReplyId);
  }

  private Integer decrementSoftAskReplyVoteAndGetVoteCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.decrementVoteCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer updateVoteCount(final Long softAskId, final Long softAskReplyId, final boolean isVoted) {
    return isVoted
      ? incrementSoftAskReplyVoteAndGetVoteCount(softAskId, softAskReplyId)
      : decrementSoftAskReplyVoteAndGetVoteCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskReplyCountAndGetReplyCount(final Long softAskId) {
    softAskRepository.incrementReplyCount(softAskId);
    return softAskRepository.getReplyCount(softAskId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskReplyChildReplyCountAndGetReplyCount(final Long softAskId, final Long softAskReplyParentId) {
    softAskReplyRepository.incrementReplyChildReplyCount(softAskId, softAskReplyParentId);
    return softAskReplyRepository.getReplyChildReplyCount(softAskId, softAskReplyParentId);
  }

  private Integer incrementBookmarkCount(final Long softAskId) {
    softAskRepository.incrementAndBookmarkCount(softAskId);
    return softAskRepository.getBookmarkCount(softAskId);
  }

  private Integer decrementBookmarkCount(final Long softAskId) {
    softAskRepository.decrementAndGetBookmarkCount(softAskId);
    return softAskRepository.getBookmarkCount(softAskId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long softAskId, final boolean isBookmarked) {
    return isBookmarked
      ? incrementBookmarkCount(softAskId)
      : decrementBookmarkCount(softAskId);
  }

  private Integer incrementSoftAskReplyBookmarkCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.incrementAndBookmarkCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getBookmarkCount(softAskId, softAskReplyId);
  }

  private Integer decrementSoftAskReplyBookmarkCount(final Long softAskId, final Long softAskReplyId) {
    softAskReplyRepository.decrementAndGetBookmarkCount(softAskId, softAskReplyId);
    return softAskReplyRepository.getBookmarkCount(softAskId, softAskReplyId);
  }

  @Override
  @Transactional
  public Integer updateBookmarkCount(final Long softAskId, final Long softAskReplyId, final boolean isBookmarked) {
    return isBookmarked
      ? incrementSoftAskReplyBookmarkCount(softAskId, softAskReplyId)
      : decrementSoftAskReplyBookmarkCount(softAskId, softAskReplyId);
  }

}
