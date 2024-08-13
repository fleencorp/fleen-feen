package com.fleencorp.feen.controller;

import com.fleencorp.feen.exception.base.BasicException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping(value = "/api")
@AllArgsConstructor
public class LanguageController {

  private final MessageSource messageSource;

  @GetMapping("/check-language")
  public String checkLanguage(@RequestHeader(name = "Accept-Language", required = false) final Locale locale) {
    System.out.println(locale + " is the locale");
    System.out.println("The message is " + messageSource.getMessage("greeting.person", null, locale));
    return messageSource.getMessage("greeting.person", null, locale);
  }

  @GetMapping("/test-error")
  public String testError() {
    if (true) {
      throw new BasicException(new Object[] {"Yusuf", "Musa"});
    }
    else {
      return "Hello World!";
    }
  }
}
