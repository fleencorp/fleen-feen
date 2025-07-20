package com.fleencorp.feen.softask.service.answer;

import com.fleencorp.feen.softask.exception.core.SoftAskAnswerNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.answer.AddSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.dto.answer.DeleteSoftAskAnswerDto;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerAddResponse;
import com.fleencorp.feen.softask.model.response.answer.SoftAskAnswerDeleteResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskAnswerService {

  SoftAskAnswerAddResponse addSoftAskAnswer(AddSoftAskAnswerDto dto, RegisteredUser user);

  SoftAskAnswerDeleteResponse deleteSoftAskAnswer(DeleteSoftAskAnswerDto deleteSoftAskAnswerDto, RegisteredUser user)
    throws SoftAskAnswerNotFoundException, SoftAskUpdateDeniedException;
}
