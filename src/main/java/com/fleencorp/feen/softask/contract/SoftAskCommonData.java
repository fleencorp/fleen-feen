package com.fleencorp.feen.softask.contract;

import com.fleencorp.feen.softask.constant.other.MoodTag;

import java.time.LocalDateTime;

public interface SoftAskCommonData {

  Long getId();

  Integer getVoteCount();

  Long getParentId();

  Long getAuthorId();

  String getUserDisplayName();

  String getAvatarUrl();

  String getParentTitle();

  Integer getBookmarkCount();

  Integer getShareCount();

  Double getLatitude();

  Double getLongitude();

  MoodTag getMoodTag();

  boolean hasLatitudeAndLongitude();

  String getUserAliasOrUsername();

  LocalDateTime getCreatedOn();

  void setContent(String content);

  void checkIsAuthor(Long userId);

  void setGeoHash(String geoHash);

  void setGeoHashPrefix(String geoHashPrefix);
}
