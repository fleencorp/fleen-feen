package com.fleencorp.feen.service.impl.external.google.firebase;

import com.google.firebase.messaging.*;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CloudNotificationService {

  private final FirebaseMessaging firebaseMessaging;

  public CloudNotificationService(final FirebaseMessaging firebaseMessaging) {
    this.firebaseMessaging = firebaseMessaging;
  }

  /**
   *
   * @throws FirebaseMessagingException
   *
   * @see <a href="https://dnai-deny.tistory.com/m/114">
   *   [FCM] Applying Firebase Cloud Messaging to Vanilla JS + FastAPI</a>
   * @see <a href="https://firebase.google.com/docs/web/alt-setup">Alternative ways to add Firebase to your JavaScript project</a>
   * @see <a href="https://firebase.google.com/docs/web/setup">Add Firebase to your JavaScript project</a>
   * @see <a href="https://firebase.google.com/docs/cloud-messaging/js/client#web_3">Set up a JavaScript Firebase Cloud Messaging client app</a>
   * @see <a href="https://velog.io/@dbfla0628/Offispace-FirebaseMessaging%EC%9D%98-sendAsync%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%98%EC%97%AC-%EB%B9%84%EB%8F%99%EA%B8%B0%EC%A0%81%EC%9C%BC%EB%A1%9C-%EC%95%8C%EB%A6%BC-%EC%A0%84%EC%B2%B4-%EC%A0%84%EC%86%A1%ED%95%98%EA%B8%B0">
   *   [OFFISPACE] Sending asynchronous push notifications using the FirebaseMessaging sendAsync method</a>
   * @see <a href="https://velog.io/@heelieben/FCM-React-Web-Push-%EA%B5%AC%ED%98%84%ED%95%98%EA%B8%B0-feat.-pwa-service-worker">
   *   Web Push | Implementing React + FCM (feat. pwa, service worker)</a>
   */
  public void sendMessage() throws FirebaseMessagingException {
    firebaseMessaging
      .sendAsync(
      Message.builder()
        .setNotification(Notification.builder().setTitle("Hello").setBody("World").build())
        .setAndroidConfig(       AndroidConfig.builder()
          .setNotification(
            AndroidNotification.builder()
              .setTitle("Hello")
              .setBody("Hello")
              .setClickAction("push_click")
              .build()
          )
          .build())
        .setApnsConfig(                        ApnsConfig.builder()
          .setAps(Aps.builder()
            .setCategory("push_click")
            .build())
          .build()
        )
        .putAllData(Map.of())
        .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
          .setNotification(
            new WebpushNotification("hello", "Hello World"))
          .build()
        )
        .setToken("eErJ9nT30ruw0JoEAlwjzy:APA91bG9wRPCmXWVHF_DWX-GM6YsM-gDUPTgrBwp8UdjSaggpw-h9yqaNhN1oGRqh_66tc27_zHU-A566IVbbbQ4aj2Az3pzXJfy3h39JuzFl2STj-Ou86cU2u4zkzE7g1wIjyK5xlh-")
        .build()
    );
  }

//  public void sendFCMNotificationAsync(String email, Message message) {
//    ApiFuture<String> apiFuture = firebaseMessaging.sendAsync(message);
//    apiFuture.addListener(() -> {
//      try {
//        String response = apiFuture.get();
//        log.info("FCM Notification Sent Successfully. Message ID: [{}]", response);
//        log.info("Current Call Back Thread Name: [{}]", Thread.currentThread().getName());
//      } catch (InterruptedException | ExecutionException executionException) {
//        if (executionException.getCause() instanceof FirebaseMessagingException firebaseMessagingException) {
//          MessagingErrorCode errorCode = firebaseMessagingException.getMessagingErrorCode();
//          if (isRetryMessagingErrorCode(errorCode)) {
//            fcmNotificationServiceProvider.getObject().sendFCMNotification(email, message);
//            return;
//          }
//          handleFCMMessagingException(errorCode, email);
//        }
//      }
//    }, callBackTaskExecutor);
//  }
}
