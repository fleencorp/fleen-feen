package com.fleencorp.feen.shared.poll.model;

import com.fleencorp.feen.shared.poll.contract.IsAPoll;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PollData implements IsAPoll {

  private Long pollId;
  private String question;
  private String description;
  private Long authorId;
  private Long parentId;
  private String parentTitle;
  private Long streamId;
  private Long chatSpaceId;

  public String getTitle() {
    return question;
  }
}
