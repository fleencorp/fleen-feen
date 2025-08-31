package com.fleencorp.feen.model.contract;

import java.util.Optional;

public interface HasTitle {

  String getTitle();

  static String getTitle(HasTitle hasTitle) {
    return Optional
      .ofNullable(hasTitle)
      .map(entity -> entity.getTitle())
      .orElse(null);
  }
}
