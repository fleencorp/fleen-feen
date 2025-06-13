package com.fleencorp.feen.service.impl.social;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.social.share.contact.*;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.notification.Notification;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.dto.social.share.ExpectShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.ProcessShareContactRequestDto;
import com.fleencorp.feen.model.dto.social.share.SendShareContactRequestDto;
import com.fleencorp.feen.model.info.share.contact.request.ShareContactRequestStatusInfo;
import com.fleencorp.feen.model.request.search.social.ShareContactRequestSearchRequest;
import com.fleencorp.feen.model.response.social.share.*;
import com.fleencorp.feen.model.search.social.share.contact.ShareContactRequestSearchResult;
import com.fleencorp.feen.repository.social.ShareContactRequestRepository;
import com.fleencorp.feen.service.impl.notification.NotificationMessageService;
import com.fleencorp.feen.service.notification.NotificationService;
import com.fleencorp.feen.service.social.ShareContactRequestService;
import com.fleencorp.feen.user.exception.user.UserNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.repository.MemberRepository;
import com.fleencorp.localizer.service.Localizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.isNull;

/**
 * Implementation of the `ShareContactRequestService` interface, responsible for handling
 * operations related to share contact requests, such as sending, processing, and searching
 * contact requests within the system.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
@Slf4j
public class ShareContactRequestServiceImpl implements ShareContactRequestService {

  private final NotificationMessageService notificationMessageService;
  private final NotificationService notificationService;
  private final MemberRepository memberRepository;
  private final ShareContactRequestRepository shareContactRequestRepository;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public ShareContactRequestServiceImpl(
      final NotificationMessageService notificationMessageService,
      final NotificationService notificationService,
      final MemberRepository memberRepository,
      final ShareContactRequestRepository shareContactRequestRepository,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.notificationMessageService = notificationMessageService;
    this.notificationService = notificationService;
    this.memberRepository = memberRepository;
    this.shareContactRequestRepository = shareContactRequestRepository;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Finds and returns a paginated list of share contact requests based on the search criteria.
   *
   * <p>The method checks if a specific status is provided in the search request. If so, it retrieves
   * share contact requests with that status. If no status is provided, it retrieves expected requests.</p>
   *
   * @param searchRequest the search criteria including page number and optional status
   * @param user the user making the request
   * @return a `ShareContactRequestSearchResult` containing the paginated list of `ShareContactRequestResponse`
   */
  @Override
  public ShareContactRequestSearchResult findSentShareContactRequests(final ShareContactRequestSearchRequest searchRequest, final RegisteredUser user) {
    // Prepare parameters
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();

    // Determine the query based on the presence of a share contact request status
    final ShareContactRequestStatus shareContactRequestStatus = searchRequest.getShareContactRequestStatus(ShareContactRequestStatus.SENT);
    // Retrieve the share contact request sent by the user
    final Page<ShareContactRequest> page = shareContactRequestRepository.findRequestsSentByMember(member, shareContactRequestStatus, pageable);

    // Convert the retrieved ShareContactRequests to ShareContactRequestResponse
    final List<ShareContactRequestResponse> shareContactRequestResponses = getSentShareContactRequests(page.getContent());
    // Create the search result
    final SearchResult searchResult = toSearchResult(shareContactRequestResponses, page);
    // Create the share contact search result
    final ShareContactRequestSearchResult shareContactRequestSearchResult = ShareContactRequestSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(shareContactRequestSearchResult);
  }

  /**
   * Finds and retrieves share contact requests made to the authenticated user.
   *
   * <p>This method converts the authenticated user ({@link RegisteredUser}) to a {@link Member},
   * and based on the given {@link ShareContactRequestSearchRequest}, it retrieves a paginated list
   * of {@link ShareContactRequest} objects that were sent to this member. The status of the requests is
   * determined by the search request, with a default status of {@link ShareContactRequestStatus#SENT} if not provided.</p>
   *
   * <p>The retrieved {@code ShareContactRequest} objects are then converted to {@link ShareContactRequestResponse} DTOs,
   * and the result is wrapped in a {@link ShareContactRequestSearchResult} which includes both the responses and pagination details.</p>
   *
   * @param searchRequest the search request object containing filters and pagination information
   * @param user the authenticated user who is the recipient of the share contact requests
   * @return a {@code ShareContactRequestSearchResult} containing the share contact requests and pagination details,
   *         or an empty result if no requests were found
   */
  @Override
  public ShareContactRequestSearchResult findReceivedShareContactRequests(final ShareContactRequestSearchRequest searchRequest, final RegisteredUser user) {
    // Prepare parameters
    final Pageable pageable = searchRequest.getPage();
    final Member member = user.toMember();

    final ShareContactRequestStatus shareContactRequestStatus = searchRequest.getShareContactRequestStatus(ShareContactRequestStatus.SENT);
    // Retrieve the share contact request made to the user
    final Page<ShareContactRequest> page = shareContactRequestRepository.findRequestsMadeToMember(member, shareContactRequestStatus, pageable);

    // Convert the retrieved ShareContactRequests to ShareContactRequestResponse
    final List<ShareContactRequestResponse> shareContactRequestResponses = getReceivedShareContactRequests(page.getContent());
    // Create the search result
    final SearchResult searchResult = toSearchResult(shareContactRequestResponses, page);
    // Create the search result
    final ShareContactRequestSearchResult shareContactRequestSearchResult = ShareContactRequestSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(shareContactRequestSearchResult);
  }

  /**
   * Finds and retrieves share contact requests initiated by the authenticated user that are expected to be received by others.
   *
   * <p>This method converts the authenticated user ({@link RegisteredUser}) to a {@link Member},
   * and retrieves a paginated list of {@link ShareContactRequest} objects that were initiated by the user.
   * These requests are expected to be received by other users.</p>
   *
   * <p>The retrieved {@code ShareContactRequest} objects are then converted to {@link ShareContactRequestResponse} DTOs,
   * and the result is wrapped in a {@link ShareContactRequestSearchResult}, which includes both the request responses and pagination details.</p>
   *
   * @param searchRequest the search request object containing filters and pagination information
   * @param user the authenticated user who initiated the share contact requests
   * @return a {@code ShareContactRequestSearchResult} containing the share contact requests and pagination details,
   *         or an empty result if no requests were found
   */
  @Override
  public ShareContactRequestSearchResult findExpectedShareContactRequests(final ShareContactRequestSearchRequest searchRequest, final RegisteredUser user) {
    final Page<ShareContactRequest> page;
    // Convert authenticated user to member
    final Member member = user.toMember();
    final List<ShareContactRequestResponse> shareContactRequestResponses;

    if (searchRequest.getIsSentExpectedRequest()) {
      // Retrieve the share contact request expected and initiated by the user
      page = shareContactRequestRepository.findExpectedRequestsMadeByMember(member, true, searchRequest.getPage());
      // Convert the retrieved ShareContactRequests to ShareContactRequestResponse
      shareContactRequestResponses = getSentShareContactRequests(page.getContent());
    } else {
      // Retrieve the share contact request expected and initiated to the user
      page = shareContactRequestRepository.findExpectedRequestsMadeToMember(member, true, searchRequest.getPage());
      // Convert the retrieved ShareContactRequests to ShareContactRequestResponse
      shareContactRequestResponses = getReceivedShareContactRequests(page.getContent());
    }

    // Create the search result
    final SearchResult searchResult = toSearchResult(shareContactRequestResponses, page);
    // Create the search result
    final ShareContactRequestSearchResult shareContactRequestSearchResult = ShareContactRequestSearchResult.of(searchResult);
    // Return a search result with the responses and pagination details
    return localizer.of(shareContactRequestSearchResult);
  }

  /**
   * Converts a list of `ShareContactRequest` entities to a list of `ShareContactRequestResponse`.
   *
   * <p>This method maps each `ShareContactRequest` to a `ShareContactRequestResponse` by extracting
   * the share contact request ID, recipient's full name and user ID.</p>
   *
   * @param shareContactRequests the list of `ShareContactRequest` entities to be converted
   * @return a list of `ShareContactRequestResponse` with the recipient's details
   */
  protected List<ShareContactRequestResponse> getSentShareContactRequests(final List<ShareContactRequest> shareContactRequests) {
    // Convert each ShareContactRequest to ShareContactRequestResponse
    return shareContactRequests
      .stream()
      .map(shareContactRequest -> {
        final Long shareContactRequestId = shareContactRequest.getShareContactRequestId();
        final String fullName = shareContactRequest.getRecipient().getFullName();
        final Long userId = shareContactRequest.getRecipient().getMemberId();
        // Get the request to share status info
        final ShareContactRequestStatusInfo requestStatusInfo = unifiedMapper.toShareContactRequestStatusInfo(shareContactRequest.getRequestStatus());

        return ShareContactRequestResponse.of(shareContactRequestId, fullName, userId, requestStatusInfo);
      })
      .toList();
  }

  /**
   * Converts a list of {@link ShareContactRequest} objects to a list of {@link ShareContactRequestResponse}.
   *
   * <p>This method processes each {@code ShareContactRequest} by extracting relevant information such as
   * the request ID, the initiator's full name, and their user ID. It then creates and returns a corresponding
   * {@code ShareContactRequestResponse} for each request.</p>
   *
   * @param shareContactRequests the list of {@code ShareContactRequest} objects to be converted; should not be {@code null}
   * @return a list of {@code ShareContactRequestResponse} objects built from the provided {@code ShareContactRequest} list
   */
  protected List<ShareContactRequestResponse> getReceivedShareContactRequests(final List<ShareContactRequest> shareContactRequests) {
    // Convert each ShareContactRequest to ShareContactRequestResponse
    return shareContactRequests
      .stream()
      .map(shareContactRequest -> {
        final Long shareContactRequestId = shareContactRequest.getShareContactRequestId();
        final String fullName = shareContactRequest.getInitiator().getFullName();
        final Long userId = shareContactRequest.getInitiator().getMemberId();
        // Get the request to share status info
        final ShareContactRequestStatusInfo requestStatusInfo = unifiedMapper.toShareContactRequestStatusInfo(shareContactRequest.getRequestStatus());

        return ShareContactRequestResponse.of(shareContactRequestId, fullName, userId, requestStatusInfo);
      })
      .toList();
  }

  /**
   * Processes and saves a new share contact request.
   *
   * <p>This method validates the recipient, converts the DTO to a `ShareContactRequest`,
   * and then saves the request to the repository.</p>
   *
   * @param expectShareContactRequestDto the DTO containing details for the share contact request
   * @param user the current user initiating the request
   * @return an instance of `ExpectShareContactRequestResponse`
   * @throws UserNotFoundException if the recipient cannot be found in the repository
   */
  @Override
  @Transactional
  public ExpectShareContactRequestResponse expectShareContactRequest(final ExpectShareContactRequestDto expectShareContactRequestDto, final RegisteredUser user) {
    // Validate that the recipient exists in the repository
    memberRepository.findById(expectShareContactRequestDto.getRecipientId())
      .orElseThrow(UserNotFoundException.of(expectShareContactRequestDto.getRecipientId()));

    // Convert DTO to ShareContactRequest
    final ShareContactRequest shareContactRequest = expectShareContactRequestDto.toShareContactRequest(user.toMember());
    // Save the share contact request to the repository
    shareContactRequestRepository.save(shareContactRequest);

    // Return response indicating the request was processed
    return localizer.of(ExpectShareContactRequestResponse.of());
  }

  /**
   * Processes a share contact request based on the given ID and DTO.
   *
   * <p>This method verifies that the user exists and retrieves the share contact request by its ID.
   * It then verifies the recipient and the status of the request, ensuring that the request has not
   * already been accepted or rejected. The method updates the request with new details and saves it.</p>
   *
   * @param shareContactRequestId the ID of the share contact request to process
   * @param processShareContactRequestDto the DTO containing details to update the share contact request
   * @param user the user processing the share contact request
   * @return `ProcessShareContactRequestResponse` indicating the successful processing of the request
   * @throws UserNotFoundException if the user is not found
   * @throws ShareContactRequestNotFoundException if the share contact request is not found
   * @throws FailedOperationException if the request is invalid or cannot be processed
   */
  @Override
  @Transactional
  public ProcessShareContactRequestResponse processShareContactRequest(final Long shareContactRequestId, final ProcessShareContactRequestDto processShareContactRequestDto, final RegisteredUser user) {
    // Validate that the user exists
    memberRepository.findById(user.getId())
      .orElseThrow(UserNotFoundException.of(user.getId()));

    // Retrieve the share contact request by its ID
    final ShareContactRequest shareContactRequest = shareContactRequestRepository.findById(shareContactRequestId)
      .orElseThrow(ShareContactRequestNotFoundException.of(shareContactRequestId));

    // Extract contact information from the DTO
    final String contact = processShareContactRequestDto.getContact();
    // Verify the recipient of the share contact request
    verifyRecipient(shareContactRequest.getRecipient(), user.toMember());
    // Extract and verify the request status and contact details
    final ShareContactRequestStatus shareContactRequestStatus = processShareContactRequestDto.getShareContactRequestStatus();
    // Ensure the request is to be accepted and if the contact to be shared is valid
    verifyContactRequestStatusAndContact(shareContactRequestStatus, contact);
    // Ensure that the request has not been accepted or rejected already
    verifyRequestHasNotBeenAcceptedOrRejected(shareContactRequestStatus);
    // Ensure that the request can only be accepted or rejected
    verifyShareContactRequestCanOnlyBeAcceptedOrRejected(shareContactRequestStatus);

    // Update the share contact request with new details
    shareContactRequest.update(
      processShareContactRequestDto.getShareContactRequestStatus(),
      processShareContactRequestDto.getContactType(),
      contact,
      processShareContactRequestDto.getComment());

    // Save the updated share contact request
    shareContactRequestRepository.save(shareContactRequest);
    // Create and save notification
    final Notification notification = notificationMessageService.ofApprovedOrDisapproved(shareContactRequest, shareContactRequest.getInitiator());
    notificationService.save(notification);

    // Get the request to share status info
    final ShareContactRequestStatusInfo requestStatusInfo = unifiedMapper.toShareContactRequestStatusInfo(shareContactRequest.getRequestStatus());

    // Return response indicating successful processing of the share contact request
    return localizer.of(ProcessShareContactRequestResponse.of(requestStatusInfo));
  }

  /**
   * Sends a share contact request.
   * Converts the DTO to a `ShareContactRequest` and saves it.
   * Returns a response indicating the successful creation of the share contact request.
   *
   * @param sendShareContactRequestDto the DTO containing details of the share contact request
   * @param user the user sending the share contact request
   * @return `SendShareContactRequestResponse` indicating the successful creation of the request
   * @throws UserNotFoundException if the recipient user is not found
   */
  @Override
  @Transactional
  public SendShareContactRequestResponse sendShareContactRequest(final SendShareContactRequestDto sendShareContactRequestDto, final RegisteredUser user) {
    // Validate that the recipient user exists
    final Member member = memberRepository.findById(sendShareContactRequestDto.getRecipientId())
      .orElseThrow(UserNotFoundException.of(sendShareContactRequestDto.getRecipientId()));

    // Convert DTO to ShareContactRequest and save it
    final ShareContactRequest shareContactRequest = sendShareContactRequestDto.toShareContactRequest(user.getId());
    shareContactRequestRepository.save(shareContactRequest);

    // Create and save notification
    final Notification notification = notificationMessageService.ofReceivedShareContactRequest(shareContactRequest, sendShareContactRequestDto.getRecipient(), member);
    notificationService.save(notification);

    // Get the request to share status info
    final ShareContactRequestStatusInfo requestStatusInfo = unifiedMapper.toShareContactRequestStatusInfo(shareContactRequest.getRequestStatus());

    // Return response indicating successful creation of the share contact request
    return localizer.of(SendShareContactRequestResponse.of(requestStatusInfo));
  }

  /**
   * Cancels a share contact request.
   * Verifies if the request has been accepted or rejected before canceling.
   * Updates the request status to `CANCELED` and saves the request.
   *
   * @param shareContactRequestId the ID of the share contact request to cancel
   * @param user the user initiating the cancellation
   * @return `CancelShareContactRequestResponse` indicating the cancellation status
   * @throws ShareContactRequestNotFoundException if the request is not found
   * @throws ShareContactRequestAlreadyCanceledException if the request is already canceled
   */
  @Override
  @Transactional
  public CancelShareContactRequestResponse cancelShareContactRequest(final Long shareContactRequestId, final RegisteredUser user) {
    // Retrieve the share contact request based on ID and user
    final ShareContactRequest shareContactRequest = shareContactRequestRepository.findByShareContactRequestIdAndInitiator(shareContactRequestId, user.toMember())
      .orElseThrow(ShareContactRequestNotFoundException.of(shareContactRequestId));

    // Check if the request is already canceled
    if (ShareContactRequestStatus.isCanceled(shareContactRequest.getRequestStatus())) {
      throw new ShareContactRequestAlreadyCanceledException();
    }
    // Verify if the request has been accepted or rejected
    verifyRequestHasBeenAcceptedOrRejected(shareContactRequest.getRequestStatus());

    // Set the request status to CANCELED and save it
    shareContactRequest.cancel();
    shareContactRequestRepository.save(shareContactRequest);
    // Get the request to share status info
    final ShareContactRequestStatusInfo requestStatusInfo = unifiedMapper.toShareContactRequestStatusInfo(shareContactRequest.getRequestStatus());

    // Return response indicating successful cancellation
    return localizer.of(CancelShareContactRequestResponse.of(requestStatusInfo));
  }

  /**
   * Verifies the contact request status and contact information.
   * If the status is `ACCEPTED`, verifies that the contact is not null.
   *
   * @param shareContactRequestStatus the status of the contact request
   * @param contact the contact information to verify if the status is `ACCEPTED`
   * @throws FailedOperationException if the status is null
   * @throws ShareContactRequestValueRequiredException if the status is `ACCEPTED` and the contact is null
   */
  protected void verifyContactRequestStatusAndContact(final ShareContactRequestStatus shareContactRequestStatus, final String contact) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, FailedOperationException::new);

    // If the status is ACCEPTED, verify the contact
    if (ShareContactRequestStatus.isAccepted(shareContactRequestStatus)) {
      verifyContact(contact);
    }
  }

  /**
   * Verifies that the contact information is not null.
   * Throws `ShareContactRequestValueRequiredException` if the contact is null.
   *
   * @param contact the contact information to verify
   * @throws ShareContactRequestValueRequiredException if the contact is null
   */
  protected void verifyContact(final String contact) {
    if (isNull(contact)) {
      throw new ShareContactRequestValueRequiredException();
    }
  }

  /**
   * Verifies if the recipient of a contact sharing request is valid.
   * If either the recipient or the current user is null, an `FailedOperationException` is thrown.
   * If the recipient's ID does not match the current user's ID, a `CannotProcessShareContactRequestException` is thrown.
   *
   * @param recipient the intended recipient of the contact sharing request
   * @param currentUser the user initiating the request
   * @throws FailedOperationException if either the recipient or the current user is null
   * @throws CannotProcessShareContactRequestException if the recipient's ID does not match the current user's ID
   */
  protected void verifyRecipient(final Member recipient, final Member currentUser) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(recipient, currentUser), FailedOperationException::new);

    // Check if the recipient's ID matches the current user's ID
    final boolean isSame = Objects.equals(recipient.getMemberId(), currentUser.getMemberId());
    if (!isSame) {
      throw new CannotProcessShareContactRequestException();
    }
  }

  /**
   * Verifies if a contact sharing request has not been confirmed or rejected.
   * If the request status is null, an `FailedOperationException` is thrown.
   * If the request has not been confirmed or rejected, a `ShareContactRequestAlreadyProcessedException` is thrown.
   *
   * @param shareContactRequestStatus the status of the contact sharing request to verify
   * @throws FailedOperationException if the status is null
   * @throws ShareContactRequestAlreadyProcessedException if the request has not been confirmed or rejected
   */
  protected void verifyRequestHasNotBeenAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, FailedOperationException::new);

    // Check if the request has not been confirmed or rejected
    if (!(ShareContactRequestStatus.isAcceptedOrRejected(shareContactRequestStatus))) {
      throw new ShareContactRequestAlreadyProcessedException();
    }
  }

  /**
   * Verifies if a contact sharing request has been confirmed or rejected.
   * If the request status is null, an `FailedOperationException` is thrown.
   * If the request has already been confirmed or rejected, a `CannotCancelShareContactRequestException` is thrown.
   *
   * @param shareContactRequestStatus the status of the contact sharing request to verify
   * @throws FailedOperationException if the status is null
   * @throws CannotCancelShareContactRequestException if the request has already been confirmed or rejected
   */
  protected void verifyRequestHasBeenAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, FailedOperationException::new);

    // Check if the request has been confirmed or rejected
    if (ShareContactRequestStatus.isAcceptedOrRejected(shareContactRequestStatus)) {
      throw new CannotCancelShareContactRequestException();
    }
  }

  /**
   * Verifies that the share contact request can only be accepted or rejected.
   *
   * <p>This method checks if the share contact request status is valid for processing.
   * It throws an exception if the status is null or if it is in a state that cannot be accepted or rejected.</p>
   *
   * @param shareContactRequestStatus the status of the share contact request to verify
   * @throws FailedOperationException if the status is null
   * @throws CannotProcessShareContactRequestException if the status is SENT or CANCELED
   */
  protected void verifyShareContactRequestCanOnlyBeAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, FailedOperationException::new);

    // Check if the status is either SENT or CANCELED, which are invalid for acceptance or rejection
    if (ShareContactRequestStatus.isSentOrCanceled(shareContactRequestStatus)) {
      throw new CannotProcessShareContactRequestException();
    }
  }

}
