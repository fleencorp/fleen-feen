package com.fleencorp.feen.softask.service.reply;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.exception.core.SoftAskReplyNotFoundException;
import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.reply.DeleteSoftAskReplyDto;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyDeleteResponse;

public interface SoftAskReplyService {

  SoftAskReplyAddResponse addSoftAskReply(AddSoftAskReplyDto dto, RegisteredUser user);

  SoftAskReplyDeleteResponse deleteSoftAskReply(Long softAskReplyId, DeleteSoftAskReplyDto deleteSoftAskReplyDto, RegisteredUser user)
    throws SoftAskReplyNotFoundException, SoftAskUpdateDeniedException, FailedOperationException;
}
