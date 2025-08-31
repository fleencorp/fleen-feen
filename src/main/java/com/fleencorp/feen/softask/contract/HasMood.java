package com.fleencorp.feen.softask.contract;

import com.fleencorp.feen.softask.model.info.core.MoodTagInfo;

public interface HasMood {

  MoodTagInfo getMoodTagInfo();

  void setMoodTagInfo(MoodTagInfo moodTagInfo);
}
