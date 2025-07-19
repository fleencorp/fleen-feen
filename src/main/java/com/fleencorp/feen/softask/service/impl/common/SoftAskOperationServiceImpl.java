package com.fleencorp.feen.softask.service.impl.common;

import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.repository.answer.SoftAskAnswerRepository;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.repository.softask.SoftAskRepository;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SoftAskOperationServiceImpl implements SoftAskOperationService {

  private final SoftAskAnswerRepository softAskAnswerRepository;
  private final SoftAskReplyRepository softAskReplyRepository;
  private final SoftAskRepository softAskRepository;

  public SoftAskOperationServiceImpl(
      final SoftAskAnswerRepository softAskAnswerRepository,
      final SoftAskReplyRepository softAskReplyRepository,
      final SoftAskRepository softAskRepository) {
    this.softAskAnswerRepository = softAskAnswerRepository;
    this.softAskReplyRepository = softAskReplyRepository;
    this.softAskRepository = softAskRepository;
  }

  @Override
  @Transactional
  public SoftAskAnswer save(final SoftAskAnswer softAskAnswer) {
    return softAskAnswerRepository.save(softAskAnswer);
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
  @Transactional
  public Integer incrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.incrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  @Override
  @Transactional
  public Integer decrementSoftAskVoteAndGetVoteCount(final Long softAskId) {
    softAskRepository.decrementVoteCount(softAskId);
    return softAskRepository.getVoteCount(softAskId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskAnswerVoteAndGetVoteCount(final Long softAskAnswerId) {
    softAskAnswerRepository.incrementVoteCount(softAskAnswerId);
    return softAskAnswerRepository.getVoteCount(softAskAnswerId);
  }

  @Override
  @Transactional
  public Integer decrementSoftAskAnswerVoteAndGetVoteCount(final Long softAskAnswerId) {
    softAskAnswerRepository.decrementVoteCount(softAskAnswerId);
    return softAskAnswerRepository.getVoteCount(softAskAnswerId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskReplyVoteAndGetVoteCount(final Long softAskReplyId) {
    softAskReplyRepository.incrementVoteCount(softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskReplyId);
  }

  @Override
  @Transactional
  public Integer decrementSoftAskReplyVoteAndGetVoteCount(final Long softAskReplyId) {
    softAskReplyRepository.decrementVoteCount(softAskReplyId);
    return softAskReplyRepository.getVoteCount(softAskReplyId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskAnswerCountAndGetAnswerCount(final Long softAskId) {
    softAskRepository.incrementAnswerCount(softAskId);
    return softAskRepository.getAnswerCount(softAskId);
  }

  @Override
  @Transactional
  public Integer incrementSoftAskAnswerReplyCountAndGetReplyCount(final Long softAskAnswerId) {
    softAskAnswerRepository.incrementReplyCount(softAskAnswerId);
    return softAskAnswerRepository.getReplyCount(softAskAnswerId);
  }

}
