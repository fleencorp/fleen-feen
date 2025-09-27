package com.fleencorp.feen.user.service.impl;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.common.service.word.bank.WordBankService;
import com.fleencorp.feen.model.domain.word.bank.Adjective;
import com.fleencorp.feen.model.domain.word.bank.Noun;
import com.fleencorp.feen.shared.common.model.GeneratedParticipantDetail;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.service.UsernameService;
import com.fleencorp.feen.user.service.member.MemberService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

import static com.fleencorp.feen.shared.util.SharedUtil.sanitize;
import static java.util.Objects.isNull;

@Service
public class UsernameServiceImpl implements UsernameService {

  private final MemberService memberService;
  private final WordBankService wordBankService;

  public UsernameServiceImpl(
      final MemberService memberService,
      final WordBankService wordBankService) {
    this.memberService = memberService;
    this.wordBankService = wordBankService;
  }

  /**
   * Finds a random adjective from the adjective repository.
   *
   * <p>This method retrieves the minimum and maximum adjective IDs from the {@code adjectiveRepository} and continuously generates a random ID
   * within that range. It attempts to find an adjective corresponding to the generated random ID, and if an adjective is found, it is returned.
   * The process repeats until a valid adjective is found.</p>
   *
   * @return a randomly selected {@code Adjective} from the adjective repository
   */
  @Override
  public GeneratedParticipantDetail generateRandomUsername() {
    final Random random = new SecureRandom();
    final Adjective adjective = wordBankService.findRandomAdjective();
    final Noun noun = wordBankService.findRandomNoun();

    if (isNull(adjective) || isNull(noun)) {
      throw FailedOperationException.of();
    }

    final int number = random.nextInt(10000);
    final String username = sanitize(adjective.getWord()) + sanitize(noun.getWord()) + number;
    final String finalUsername = username.trim();

    final String displayName = GeneratedParticipantDetail.createDisplayName(adjective.getWord(), noun.getWord());
    final String displayName2 = GeneratedParticipantDetail.createOtherDisplayName(adjective.getWord(), noun.getWord(), number);

    return GeneratedParticipantDetail.of(finalUsername, displayName, displayName2, null);
  }

  /**
   * Assigns a unique username to the provided member.
   *
   * <p>This method generates a random username and checks its uniqueness using the {@code memberService}.
   * If a unique username is found within the allowed number of attempts, it is assigned to the member.
   * If no unique username is found after {@code maxAttempts}, a {@code FailedOperationException} is thrown.</p>
   *
   * @param member the member to whom the unique username will be assigned
   * @throws FailedOperationException if a unique username cannot be generated within the allowed attempts
   */
  @Override
  @Transactional
  public void assignUniqueUsername(final Member member) {
    final int maxAttempts = 10;
    int attempts = 0;

    while (attempts < maxAttempts) {
      final GeneratedParticipantDetail generatedParticipantDetail = generateRandomUsername();
      final String username = generatedParticipantDetail.username();

      if (!memberService.isUsernameExist(username)) {
        member.setUsername(username);
        return;
      }
      attempts++;
    }

    throw FailedOperationException.of();
  }
}
