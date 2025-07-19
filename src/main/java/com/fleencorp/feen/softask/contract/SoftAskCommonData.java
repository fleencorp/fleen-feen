package com.fleencorp.feen.softask.contract;

public interface SoftAskCommonData {

  Long getId();

  Integer getVoteCount();

  Long getParentId();

  String getParentTitle();

  String getUserOtherName();

  void setContent(String content);

  void checkIsAuthor(Long userId);
}
