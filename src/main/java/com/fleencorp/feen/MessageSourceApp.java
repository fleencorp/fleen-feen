package com.fleencorp.feen;

import com.fleencorp.feen.exception.auth.AlreadySignedUpException;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessageSourceApp {

  public static void main(String[] args) {
    // Define the base name of the resource bundle using dot notation
    String baseName = "i18n.errors.messages"; // Correct usage with dots

    Locale locale = Locale.US; // for English (United States)
    // Locale locale = Locale.FRANCE; // for French (France)

    ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);

    AlreadySignedUpException alreadySignedUpException = new AlreadySignedUpException();
    if (bundle.containsKey(alreadySignedUpException.getMessageCode())) {
      String message = bundle.getString(alreadySignedUpException.getMessageCode());
      System.out.println("Resolved Message: " + message);
    } else {
      System.out.println("Message key not found in the resource bundle.");
    }
  }
}
