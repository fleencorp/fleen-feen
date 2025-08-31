package com.fleencorp.feen.common.event.model.stream;

import com.fleencorp.feen.common.constant.base.ResultType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResultData {

  private String userId;
  private ResultType resultType;
}
