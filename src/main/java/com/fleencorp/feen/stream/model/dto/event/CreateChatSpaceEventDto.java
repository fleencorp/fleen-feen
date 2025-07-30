package com.fleencorp.feen.stream.model.dto.event;

import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.model.dto.core.CreateStreamDto;
import com.fleencorp.feen.user.model.domain.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.stream.constant.core.StreamSource.GOOGLE_MEET;
import static com.fleencorp.feen.stream.constant.core.StreamType.EVENT;

@Getter
@Setter
@NoArgsConstructor
public class CreateChatSpaceEventDto extends CreateStreamDto {

  public FleenStream toStream(final Member member, final ChatSpace chatSpace) {
    final FleenStream stream = toFleenStream();
    stream.setMember(member);
    stream.setChatSpaceId(chatSpace.getChatSpaceId());
    stream.setChatSpace(chatSpace);
    stream.setStreamType(EVENT);
    stream.setStreamSource(GOOGLE_MEET);

    return stream;
  }
}
