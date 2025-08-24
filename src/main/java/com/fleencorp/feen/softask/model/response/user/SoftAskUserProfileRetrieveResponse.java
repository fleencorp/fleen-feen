package com.fleencorp.feen.softask.model.response.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskConversationVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskReplyVotedInfo;
import com.fleencorp.feen.softask.model.info.vote.total.TotalSoftAskVotedInfo;
import com.fleencorp.feen.softask.model.search.SoftAskReplySearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.model.search.SoftAskVoteSearchResult;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "total_soft_ask_reply_voted_info",
  "total_soft_ask_voted_info",
  "total_soft_ask_conversation_voted_info",
  "soft_ask_search_result",
  "reply_search_result",
  "vote_search_result"
})
public class SoftAskUserProfileRetrieveResponse extends LocalizedResponse {

  @JsonProperty("reply_search_result")
  private SoftAskReplySearchResult softAskReplySearchResult = SoftAskReplySearchResult.empty();

  @JsonProperty("soft_ask_search_result")
  private SoftAskSearchResult softAskSearchResult = SoftAskSearchResult.empty();

  @JsonProperty("vote_search_result")
  private SoftAskVoteSearchResult softAskVoteSearchResult = SoftAskVoteSearchResult.empty();

  @JsonProperty("total_soft_ask_reply_voted_info")
  private TotalSoftAskReplyVotedInfo totalSoftAskReplyVotedInfo;

  @JsonProperty("total_soft_ask_voted_info")
  private TotalSoftAskVotedInfo totalSoftAskVotedInfo;

  @JsonProperty("total_soft_ask_conversation_voted_info")
  private TotalSoftAskConversationVotedInfo totalSoftAskConversationVotedInfo;

  @Override
  public String getMessageCode() {
    return "soft.ask.user.profile.retrieve";
  }

  public static SoftAskUserProfileRetrieveResponse of() {
    return new SoftAskUserProfileRetrieveResponse();
  }
}
