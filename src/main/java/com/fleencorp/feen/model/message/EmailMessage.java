package com.fleencorp.feen.model.message;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {

  private String from;
  private String to;
  private String subject;
  private String htmlText;
  private String plainText;

  public static EmailMessage of(final String from, final String to, final String subject, final String htmlText) {
    return EmailMessage.builder()
        .from(from)
        .to(to)
        .subject(subject)
        .htmlText(htmlText)
        .build();
  }
}
