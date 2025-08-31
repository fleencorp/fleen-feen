package com.fleencorp.feen.softask.service.reply;

import com.fleencorp.feen.softask.exception.core.SoftAskUpdateDeniedException;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.reply.DeleteSoftAskReplyDto;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyDeleteResponse;
import com.fleencorp.feen.user.model.security.RegisteredUser;

public interface SoftAskReplyService {

  SoftAskReplyAddResponse addSoftAskReply(AddSoftAskReplyDto dto, RegisteredUser user);

  SoftAskReplyDeleteResponse deleteSoftAskReply(final Long softAskReplyId, DeleteSoftAskReplyDto deleteSoftAskReplyDto, RegisteredUser user) throws SoftAskUpdateDeniedException;
}
