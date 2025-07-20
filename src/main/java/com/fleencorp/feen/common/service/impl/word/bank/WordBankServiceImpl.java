package com.fleencorp.feen.common.service.impl.word.bank;

import com.fleencorp.feen.model.domain.word.bank.Adjective;
import com.fleencorp.feen.model.domain.word.bank.Noun;
import com.fleencorp.feen.common.repository.word.bank.AdjectiveRepository;
import com.fleencorp.feen.common.repository.word.bank.NounRepository;
import com.fleencorp.feen.common.service.word.bank.WordBankService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
public class WordBankServiceImpl implements WordBankService {

  private final AdjectiveRepository adjectiveRepository;
  private final NounRepository nounRepository;

  public WordBankServiceImpl(
      final AdjectiveRepository adjectiveRepository,
      final NounRepository nounRepository) {
    this.adjectiveRepository = adjectiveRepository;
    this.nounRepository = nounRepository;
  }

  /**
   * Retrieves a random {@link Adjective} from the repository.
   *
   * <p>This method generates a random ID within the range of adjective IDs in the repository and retrieves the corresponding
   * adjective. It continues generating random IDs until it finds a valid {@code Adjective} that exists in the repository.
   * The method relies on the {@code adjectiveRepository} to fetch the minimum and maximum adjective IDs and find an
   * adjective by its ID.</p>
   *
   * @return a randomly selected {@code Adjective} from the repository
   */
  @Override
  public Adjective findRandomAdjective() {
    final Random random = new SecureRandom();
    final int minId = adjectiveRepository.findMinAdjectiveId();
    final int maxId = adjectiveRepository.findMaxAdjectiveId();

    while (true) {
      final int randomId = random.nextInt(maxId - minId + 1) + minId;
      final Optional<Adjective> adjective = adjectiveRepository.findAdjectiveById(randomId);
      if (adjective.isPresent()) {
        return adjective.get();
      }
    }
  }

  /**
   * Finds a random noun from the noun repository.
   *
   * <p>This method retrieves the minimum and maximum noun IDs from the {@code nounRepository} and continuously generates a random ID
   * within that range. It attempts to find a noun corresponding to the generated random ID, and if a noun is found, it is returned.
   * The process repeats until a valid noun is found.</p>
   *
   * @return a randomly selected {@code Noun} from the noun repository
   */
  @Override
  public Noun findRandomNoun() {
    final Random random = new SecureRandom();
    final int minId = nounRepository.findMinNounId();
    final int maxId = nounRepository.findMaxNounId();

    while (true) {
      final int randomId = random.nextInt(maxId - minId + 1) + minId;
      final Optional<Noun> noun = nounRepository.findNounById(randomId);
      if (noun.isPresent()) {
        return noun.get();
      }
    }
  }
}
