package com.fleencorp.feen.event.handler;

import com.fleencorp.feen.model.request.auth.SignUpVerificationRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

public class ProfileRequestHandler {


  @TransactionalEventListener(phase = AFTER_COMMIT)
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Async
  public void handleSendSignUpVerificationCode(SignUpVerificationRequest signUpVerificationRequest) {

  }
}
