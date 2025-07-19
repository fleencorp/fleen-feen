package com.fleencorp.feen.softask.model.dto.answer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
public class AddSoftAskAnswerDto {

  @NotBlank(message = "{softAskAnswer.content.NotBlank}")
  @Size(min = 10, max = 4000, message = "{softAskAnswer.content.Size}")
  @JsonProperty("answer")
  private String content;

  @NotNull(message = "{softAskAnswer.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskParentDto parent;

  private boolean hasParent() {
    return nonNull(parent);
  }

  public Long getSoftAskId() {
    return hasParent() ? parent.getParentId() : null;
  }

  public SoftAskAnswer toSoftAskAnswer(final Member author, final SoftAsk softAsk) {
    final SoftAskAnswer answer = new SoftAskAnswer();
    answer.setContent(content);
    answer.setAuthor(author);
    answer.setUserOtherName(author.getUsername());
    answer.setSoftAskId(softAsk.getSoftAskId());
    answer.setSoftAsk(softAsk);

    return answer;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskParentDto {

    @NotNull(message = "{softAskAnswer.parentId.NotNull}")
    @IsNumber(message = "{softAskAnswer.parentId.IsNumber}")
    @JsonProperty(value = "parent_id")
    private String parentId;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }
  }
}

