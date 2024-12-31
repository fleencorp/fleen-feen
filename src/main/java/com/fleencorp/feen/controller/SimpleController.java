package com.fleencorp.feen.controller;

import com.fleencorp.feen.constant.stream.StreamCreationType;
import com.fleencorp.feen.constant.stream.StreamSource;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.service.impl.external.google.chat.GoogleChatServiceImpl;
import com.fleencorp.feen.service.impl.external.google.firebase.CloudNotificationService;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/simple")
@RequiredArgsConstructor
public class SimpleController {

  private final GoogleChatServiceImpl googleChatService;
  private final CloudNotificationService cloudNotificationService;

  @GetMapping(value = "/3")
  public Object two() throws FirebaseMessagingException {
    cloudNotificationService.sendMessage();
    return "Hello World!";
  }

  @GetMapping(value = "/1")
  public Object just1() {
//    final CreateChatSpaceRequest createChatSpaceRequest = CreateChatSpaceRequest.of("Friday Space & Tonight is Raw", "", "");
//    googleChatService.createSpace(createChatSpaceRequest);
///*    CreateChatSpaceRequest createChatSpaceRequest = CreateChatSpaceRequest.of("Tuesday Space & Tonight is Raw");
//    googleChatService.retrieveSpace(RetrieveChatSpaceRequest.of(GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern("AAAASOkD-Ew")));
//    googleChatService.updateChatSpace(UpdateChatSpaceRequest
//      .of(GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern("AAAASOkD-Ew"), "Wednesday Raw", "Game", "Game II"));

//    googleChatService.addMember(AddChatSpaceMemberRequest.of("alamu@fleencorp.com", GoogleApiUtil.getChatSpaceIdOrNameRequiredPattern("AAAA-5VUp9g")));
//    googleChatService.removeMember(DeleteChatSpaceMemberRequest.of("AAAA-5VUp9g", "110651689154390137892"));
//    googleChatService.addMember(AddChatSpaceMemberRequest.of("volunux@gmail.com", "spaces/AAAAQNJasDI"));*/
//    googleChatService.justMessage();

    final FleenStream stream = FleenStream.builder()
      .scheduledStartDate(LocalDateTime.now())
      .scheduledEndDate(LocalDateTime.now().plusHours(5))
      .timezone("Africa/Lagos")
      .streamSource(StreamSource.GOOGLE_MEET)
      .streamCreationType(StreamCreationType.SCHEDULED)
      .streamVisibility(StreamVisibility.PUBLIC)
      .description("Java Annual Conference for Software Developers & Engineers in Nigeria")
      .title("Java Conf 2024")
      .streamLink("https://www.google.com/")
      .forKids(true)
      .deleted(false)
      .organizerEmail("yusuf@fleencorp.com")
      .organizerPhone("123456")
      .organizerName("Yusuf Musa")
      .chatSpace(ChatSpace.builder().externalIdOrName("spaces/AAAALt0_kws").build())
      .build();

    // Prepare the request to send a calendar event message to the chat space
/*    final GoogleChatSpaceMessageRequest googleChatSpaceMessageRequest = GoogleChatSpaceMessageRequest.ofEventOrStream(
      stream.getSpaceIdOrName(),
      requireNonNull(toFleenStreamResponse(stream))
    );

    // Send the event message to the chat space
    googleChatService.createCalendarEventMessageAndSendToChatSpace(googleChatSpaceMessageRequest);*/
    return "Hello World!";
  }

  @GetMapping(value = "/2")
  public Object just2() {
//    googleChatService.justMessage2();
    return "Hello World!";
  }
}
