package com.fleencorp.feen.event.model.stream;

import com.fleencorp.feen.constant.base.ResultType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultData {

  private String userId;
  private ResultType resultType;
}
