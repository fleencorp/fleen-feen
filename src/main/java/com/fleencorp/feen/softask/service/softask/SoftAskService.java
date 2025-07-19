package com.fleencorp.feen.softask.service.softask;

import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.dto.softask.DeleteSoftAskDto;
import com.fleencorp.feen.softask.model.response.softask.SoftAskAddResponse;
import com.fleencorp.feen.softask.model.response.softask.SoftAskDeleteResponse;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskService {

  SoftAskAddResponse addSoftAsk(AddSoftAskDto addSoftAskDto, RegisteredUser user)
    throws MemberNotFoundException, StreamNotFoundException, ChatSpaceNotFoundException;

  SoftAskDeleteResponse deleteSoftAsk(DeleteSoftAskDto deleteSoftAskDto, RegisteredUser user) throws SoftAskUpdateDeniedException;
}
