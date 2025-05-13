package com.fleencorp.feen.model.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Updatable {

  @JsonIgnore
  default Long getOrganizerId() {
    return 0L;
  }

  @JsonIgnore
  default Long getAuthorId() {
    return 0L;
  }

  void setIsUpdatable(boolean isUpdatable);

  void markAsUpdatable();
}
