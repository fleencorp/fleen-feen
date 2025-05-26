package com.fleencorp.feen.service.impl.notification;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.constant.notification.NotificationStatus;
import com.fleencorp.feen.constant.notification.NotificationType;
import com.fleencorp.feen.exception.notification.NotificationNotFoundException;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.response.notification.NotificationResponse;
import com.fleencorp.feen.model.response.notification.ReadNotificationResponse;
import com.fleencorp.feen.model.search.notification.NotificationSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.notification.NotificationRepository;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.constant.notification.NotificationType.*;
import static com.fleencorp.feen.model.response.notification.NotificationResponse.toNotificationResponse;
import static java.util.Objects.nonNull;

/**
 * Service implementation for managing notifications.
 * Provides methods for creating, saving, and managing notification entities.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class NotificationServiceImpl implements NotificationService {

  private final NotificationMessageService notificationMessageService;
  private final NotificationRepository notificationRepository;
  private final Localizer localizer;

  /**
   * Constructs a new {@code NotificationServiceImpl} with the given notification repository.
   *
   * @param notificationRepository the {@link NotificationRepository} used to perform CRUD operations on notifications
   */
  public NotificationServiceImpl(
      final NotificationMessageService notificationMessageService,
      final NotificationRepository notificationRepository,
      final Localizer localizer) {
    this.notificationMessageService = notificationMessageService;
    this.notificationRepository = notificationRepository;
    this.localizer = localizer;
  }

  /**
   * Retrieves and processes a paginated list of {@link Notification} objects based on the given search criteria,
   * converting them into {@link NotificationResponse} objects and packaging them into a {@link NotificationSearchResult}.
   *
   * <p>This method queries the {@link NotificationRepository} to find notifications based on the {@link SearchRequest}'s
   * pagination settings. It then converts the list of {@link Notification} objects to a list of {@link NotificationResponse} objects.
   * Finally, it returns a {@link NotificationSearchResult} containing the responses and pagination details.
   * If no notifications are found, an empty search result is returned.</p>
   *
   * @param searchRequest the search criteria, including pagination settings.
   * @param user the current {@link FleenUser} requesting the notifications.
   * @return a {@link NotificationSearchResult} containing the list of {@link NotificationResponse} objects and pagination details,
   *         or an empty result if no notifications are found.
   */
  @Override
  public NotificationSearchResult findNotifications(final SearchRequest searchRequest, final FleenUser user) {
    // Find a list of notifications based on the page details in the search request
    final Page<Notification> page = notificationRepository.findMany(user.toMember(), searchRequest.getPage());
    // Convert the notifications to a list of notification responses
    final List<NotificationResponse> notificationResponses = toNotificationResponses(page.getContent());
    // Create a search result
    final SearchResult searchResult = toSearchResult(notificationResponses, page);
    // Create a search result with the responses and pagination details
    final NotificationSearchResult notificationSearchResult = NotificationSearchResult.of(searchResult);
    // Return the search result
    return localizer.of(notificationSearchResult);
  }

  /**
   * Asynchronously saves a notification to the repository.
   *
   * <p>This method saves the provided {@link Notification} to the notification repository,
   * if the notification is not null. It runs asynchronously, allowing other operations
   * to continue without waiting for this process to complete.</p>
   *
   * @param notification the notification entity to be saved
   * @throws IllegalArgumentException if the notification is null
   */
  @Override
  @Async
  @Transactional
  public void save(final Notification notification) {
    if (nonNull(notification)) {
      notificationRepository.save(notification);
    }
  }

  /**
   * Marks a {@link Notification} as read for the specified {@link FleenUser} and returns a localized response.
   *
   * <p>This method retrieves the {@link Notification} with the given ID from the {@link NotificationRepository}.
   * If the notification is not found, a {@link NotificationNotFoundException} is thrown.
   * If the {@link FleenUser} is the owner of the notification, the notification's status is updated to "read"
   * and saved back to the repository. Afterward, a localized response is returned, indicating the result.</p>
   *
   * @param notificationId the ID of the {@link Notification} to be marked as read.
   * @param user the {@link FleenUser} who owns the notification.
   * @return a {@link ReadNotificationResponse} containing the result of the operation.
   * @throws NotificationNotFoundException if the notification with the given ID does not exist.
   */
  @Override
  @Transactional
  public ReadNotificationResponse markAsRead(final Long notificationId, final FleenUser user) {
    // Retrieve the notification by ID, or throw an exception if it doesn't exist
    final Notification notification = notificationRepository.findById(notificationId)
      .orElseThrow(NotificationNotFoundException.of(notificationId));

    // Check if the user is the owner of the notification
    if (notification.isOwner(user.getId())) {
      // Mark the notification as read and save it
      notification.markAsRead();
      notificationRepository.save(notification);
    }

    // Return the localized response indicating the notification has been read
    return localizer.of(ReadNotificationResponse.of());
  }

  /**
   * Marks all notifications for the specified user as read.
   *
   * @param user the user whose notifications are to be marked as read.
   * @return a {@link ReadNotificationResponse} indicating the result of the operation.
   */
  @Override
  public ReadNotificationResponse markAllAsRead(final FleenUser user) {
    // Mark all notifications currently unread as now read for the given user
    notificationRepository.markAllAsRead(NotificationStatus.read(), NotificationStatus.unread(), user.toMember());
    // Return a response indicating that the notifications have been marked as read
    return localizer.of(ReadNotificationResponse.of());
  }

  /**
   * Converts a list of {@link Notification} objects into a list of {@link NotificationResponse} objects,
   * each containing the appropriate message.
   *
   * <p>This method filters out any null notifications from the provided list, converts each remaining
   * {@link Notification} to a {@link NotificationResponse} by calling {@link #createNotificationResponseWithMessage(Notification)},
   * and ensures that only non-null responses are included in the final list.</p>
   *
   * @param notifications the list of {@link Notification} objects to be converted into responses.
   * @return a list of {@link NotificationResponse} objects with messages, excluding null values.
   */
  public List<NotificationResponse> toNotificationResponses(final List<Notification> notifications) {
    if (nonNull(notifications)) {
      return notifications.stream()
        .filter(Objects::nonNull)
        .map(this::createNotificationResponseWithMessage)  // Extracted method for conversion and message setting
        .filter(Objects::nonNull)
        .toList();
    }
    return List.of();
  }

  /**
   * Creates a {@link NotificationResponse} object from a {@link Notification} and sets the appropriate message.
   *
   * <p>This method converts the given {@link Notification} into a {@link NotificationResponse} and retrieves
   * a message handler for the notification type. If a message handler is found, it generates the message
   * using the handler and sets it in the {@link NotificationResponse}.</p>
   *
   * <p>The message handler is selected based on the {@link NotificationType} of the provided notification.</p>
   *
   * @param notification the {@link Notification} object to be converted into a response.
   * @return a {@link NotificationResponse} with the generated message if applicable.
   */
  public NotificationResponse createNotificationResponseWithMessage(final Notification notification) {
    // Convert notification to response
    final NotificationResponse notificationResponse = toNotificationResponse(notification);
    // Get the appropriate message handler
    final Function<Notification, String> messageHandler = messageHandlers().get(notification.getNotificationType());

    // If message handler is present, set the message in the response
    if (nonNull(messageHandler) && nonNull(notificationResponse)) {
      notificationResponse.setMessage(messageHandler.apply(notification));
    }

    return notificationResponse;
  }

  /**
   * Provides a mapping of {@link NotificationType} to corresponding message handling functions.
   *
   * <p>This method creates and returns a map where each {@link NotificationType} is associated with
   * a function that generates the appropriate message for a given notification. Each message is
   * generated using the appropriate method from the {@link NotificationMessageService}.</p>
   *
   * @return a map that associates each {@link NotificationType} with its respective message handler.
   */
  protected Map<NotificationType, Function<Notification, String>> messageHandlers() {
    final Map<NotificationType, Function<Notification, String>> messageHandlers = new EnumMap<>(NotificationType.class);

    // Mapping NotificationTypes to corresponding message handling functions
    // Handling requests to join a chat space
    messageHandlers.put(requestToJoinChatSpaceApproved(), notificationMessageService::ofApprovedRequestToJoinChatSpace);
    messageHandlers.put(requestToJoinChatSpaceDisapproved(), notificationMessageService::ofDisapprovedRequestToJoinChatSpace);
    messageHandlers.put(requestToJoinChatSpaceReceived(), notificationMessageService::ofReceivedRequestToJoinChatSpace);

    // Handling requests to join an event
    messageHandlers.put(requestToJoinEventApproved(), notificationMessageService::ofApprovedRequestToJoinEvent);
    messageHandlers.put(requestToJoinEventDisapproved(), notificationMessageService::ofDisapprovedRequestToJoinEvent);
    messageHandlers.put(requestToJoinEventReceived(), notificationMessageService::ofReceivedRequestToJoinEvent);

    // Handling requests to join a live broadcast
    messageHandlers.put(requestToJoinLiveBroadcastApproved(), notificationMessageService::ofApprovedRequestToJoinLiveBroadcast);
    messageHandlers.put(requestToJoinLiveBroadcastDisapproved(), notificationMessageService::ofDisapprovedRequestToJoinLiveBroadcast);
    messageHandlers.put(requestToJoinLiveBroadcastReceived(), notificationMessageService::ofReceivedRequestToJoinLiveBroadcast);

    // Handling contact sharing requests
    messageHandlers.put(shareContactRequestApproved(), notificationMessageService::ofApprovedShareContactRequest);
    messageHandlers.put(shareContactRequestDisapproved(), notificationMessageService::ofDisapprovedShareContactRequest);
    messageHandlers.put(shareContactRequestReceived(), notificationMessageService::ofReceivedShareContactRequest);

    // Handling notifications related to user following
    messageHandlers.put(userFollowing(), notificationMessageService::ofUserFollowing);

    return messageHandlers;
  }

}
