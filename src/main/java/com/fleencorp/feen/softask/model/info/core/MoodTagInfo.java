package com.fleencorp.feen.softask.model.info.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "mood",
  "mood_text",
  "mood_text_2"
})
public class MoodTagInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("mood")
  private MoodTag moodTag;

  @JsonProperty("mood_text")
  private String moodText;

  @JsonProperty("mood_text_2")
  private String moodText2;

  public static MoodTagInfo of(final MoodTag moodTag, final String moodText, final String moodText2) {
    return new MoodTagInfo(moodTag, moodText, moodText2);
  }
}
