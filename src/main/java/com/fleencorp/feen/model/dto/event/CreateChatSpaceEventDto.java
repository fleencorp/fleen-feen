package com.fleencorp.feen.model.dto.event;

import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.stream.base.CreateStreamDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.constant.stream.StreamSource.GOOGLE_MEET;
import static com.fleencorp.feen.constant.stream.StreamType.EVENT;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class CreateChatSpaceEventDto extends CreateStreamDto {

  public FleenStream toFleenStream(final Member member, final ChatSpace chatSpace) {
    final FleenStream fleenStream = toFleenStream();
    fleenStream.setMember(member);
    fleenStream.setChatSpace(chatSpace);
    fleenStream.setStreamType(EVENT);
    fleenStream.setStreamSource(GOOGLE_MEET);
    return fleenStream;
  }
}
