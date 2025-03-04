package com.fleencorp.feen.service.impl.user;

import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.domain.word.bank.Adjective;
import com.fleencorp.feen.model.domain.word.bank.Noun;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.feen.service.user.UsernameService;
import com.fleencorp.feen.service.user.WordBankService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;

@Service
public class UsernameServiceImpl implements UsernameService {

  private final MemberService memberService;
  private final WordBankService wordBankService;
  private final ThreadLocalRandom random = ThreadLocalRandom.current();

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
  private String generateRandomUsername() {
    final Adjective adjective = wordBankService.findRandomAdjective();
    final Noun noun = wordBankService.findRandomNoun();

    if (isNull(adjective) || isNull(noun)) {
      throw FailedOperationException.of();
    }

    final int number = random.nextInt(10000);
    return adjective.getWord() + noun.getWord() + number;
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
      final String username = generateRandomUsername();
      if (!memberService.isUsernameExist(username)) {
        member.setUsername(username);
        return;
      }
      attempts++;
    }

    throw FailedOperationException.of();
  }
}
