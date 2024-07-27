package com.fleencorp.feen.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/message")
@Slf4j
public class SimpleController {


  @GetMapping(value = "/post-message")
  public void postMessage() {
    log.error("Hello World!!!");
    log.warn("I am right here. Hello World!!!");
  }
}
