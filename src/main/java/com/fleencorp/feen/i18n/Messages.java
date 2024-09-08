package com.fleencorp.feen.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

  public static String getMessageForLocale(final String messageKey, final Locale locale) {
    return ResourceBundle.getBundle("messages", locale)
      .getString(messageKey);
  }
}
