package com.fleencorp.feen.stream.model.dto.event;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.core.CreateStreamDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.stream.constant.core.StreamSource.GOOGLE_MEET;
import static com.fleencorp.feen.stream.constant.core.StreamType.EVENT;

@Getter
@Setter
@NoArgsConstructor
public class CreateChatSpaceEventDto extends CreateStreamDto {

  public FleenStream toStream(final IsAMember member, final ChatSpace chatSpace) {
    final FleenStream stream = toFleenStream();
    stream.setMemberId(member.getMemberId());
    stream.setChatSpaceId(chatSpace.getChatSpaceId());
    stream.setStreamType(EVENT);
    stream.setStreamSource(GOOGLE_MEET);

    return stream;
  }
}
