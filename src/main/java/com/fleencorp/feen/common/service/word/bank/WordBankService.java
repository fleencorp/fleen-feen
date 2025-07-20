package com.fleencorp.feen.common.service.word.bank;

import com.fleencorp.feen.model.domain.word.bank.Adjective;
import com.fleencorp.feen.model.domain.word.bank.Noun;

public interface WordBankService {

  Adjective findRandomAdjective();

  Noun findRandomNoun();
}
