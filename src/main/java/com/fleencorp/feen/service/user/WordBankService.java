package com.fleencorp.feen.service.user;

import com.fleencorp.feen.model.domain.word.bank.Adjective;
import com.fleencorp.feen.model.domain.word.bank.Noun;

public interface WordBankService {

  Adjective findRandomAdjective();

  Noun findRandomNoun();
}
