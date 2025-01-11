package com.fleencorp.feen.model.info.schedule;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.StreamTimeType;
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
  "schedule_time_type",
  "schedule_time_type_text"
})
public class ScheduleTimeTypeInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("schedule_time_type")
  private StreamTimeType scheduleTimeType;

  @JsonProperty("schedule_time_type_text")
  private String scheduleTimeTypeText;

  public static ScheduleTimeTypeInfo of(final StreamTimeType scheduleTimeType, final String scheduleTimeTypeText) {
    return new ScheduleTimeTypeInfo(scheduleTimeType, scheduleTimeTypeText);
  }
}

