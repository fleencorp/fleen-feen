package com.fleencorp.feen.service.message;

public interface EmailMessageService {


  void sendMessage(String from, String to, String subject, String htmlText);

  void sendMessage(String to, String subject, String messageBody);
}
