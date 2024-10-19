package com.fleencorp.feen.model.request.chat.space;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSpaceRequest {

  protected String accessToken;


  /**
   * The name of the chat app or bot user
   *
   * @return the name of the chat app or bot user
   *
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/User#:~:text=users%2Fapp%20can%20be%20used,profile%20ID%20in%20People%20API.">
   *   User</a>
   */
  public String getChatAppOrBotName() {
    return "users/app";
  }
}
