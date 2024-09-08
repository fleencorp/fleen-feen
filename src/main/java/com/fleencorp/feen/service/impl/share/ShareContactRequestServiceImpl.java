package com.fleencorp.feen.service.impl.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.share.ShareContactRequestStatus;
import com.fleencorp.feen.exception.share.*;
import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.model.domain.share.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.share.share.ExpectShareContactRequestDto;
import com.fleencorp.feen.model.dto.share.share.ProcessShareContactRequestDto;
import com.fleencorp.feen.model.dto.share.share.SendShareContactRequestDto;
import com.fleencorp.feen.model.request.search.share.ShareContactRequestSearchRequest;
import com.fleencorp.feen.model.response.share.share.*;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.share.ShareContactRequestRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.share.ShareContactRequestService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.ExceptionUtil.checkIsNullAny;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Implementation of the `ShareContactRequestService` interface, responsible for handling
 * operations related to share contact requests, such as sending, processing, and searching
 * contact requests within the system.
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class ShareContactRequestServiceImpl implements ShareContactRequestService {

  private final MemberRepository memberRepository;
  private final ShareContactRequestRepository shareContactRequestRepository;

  /**
   * Constructs an instance of `ShareContactRequestServiceImpl` with the specified repositories.
   *
   * @param memberRepository the repository for accessing member data
   * @param shareContactRequestRepository the repository for accessing share contact request data
   */
  public ShareContactRequestServiceImpl(
    final MemberRepository memberRepository,
    final ShareContactRequestRepository shareContactRequestRepository) {
    this.memberRepository = memberRepository;
    this.shareContactRequestRepository = shareContactRequestRepository;
  }

  /**
   * Finds and returns a paginated list of share contact requests based on the search criteria.
   *
   * <p>The method checks if a specific status is provided in the search request. If so, it retrieves
   * share contact requests with that status. If no status is provided, it retrieves expected requests.</p>
   *
   * @param searchRequest the search criteria including page number and optional status
   * @param user the user making the request
   * @return a `SearchResultView` containing the paginated list of `ShareContactRequestResponse`
   */
  @Override
  public SearchResultView findShareContactRequests(final ShareContactRequestSearchRequest searchRequest, final FleenUser user) {
    final Page<ShareContactRequest> page;
    final Member member = user.toMember();

    // Determine the query based on the presence of a share contact request status
    if (nonNull(searchRequest.getShareContactRequestStatus())) {
      final ShareContactRequestStatus shareContactRequestStatus = searchRequest.getActualShareContactRequestStatus(ShareContactRequestStatus.SENT);
      page = shareContactRequestRepository.findByInitiatorAndShareContactRequestStatus(member, shareContactRequestStatus, searchRequest.getPage());
    } else {
      page = shareContactRequestRepository.findByInitiatorAndIsExpected(member, true, searchRequest.getPage());
    }

    // Convert the retrieved ShareContactRequests to ShareContactRequestResponse
    final List<ShareContactRequestResponse> views = getShareContactRequests(page.getContent());
    return toSearchResult(views, page);
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
  protected List<ShareContactRequestResponse> getShareContactRequests(final List<ShareContactRequest> shareContactRequests) {
    // Convert each ShareContactRequest to ShareContactRequestResponse
    return shareContactRequests
        .stream()
        .map(shareContactRequest -> {
          final Long shareContactRequestId = shareContactRequest.getShareContactRequestId();
          final String fullName = shareContactRequest.getRecipient().getFullName();
          final Long userId = shareContactRequest.getRecipient().getMemberId();

          return ShareContactRequestResponse.of(shareContactRequestId, fullName, userId);
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
  public ExpectShareContactRequestResponse expectShareContactRequest(final ExpectShareContactRequestDto expectShareContactRequestDto, final FleenUser user) {
    // Validate that the recipient exists in the repository
    memberRepository.findById(expectShareContactRequestDto.getRecipientId())
      .orElseThrow(() -> new UserNotFoundException(expectShareContactRequestDto.getRecipientId()));

    // Convert DTO to ShareContactRequest and save it to the repository
    final ShareContactRequest shareContactRequest = expectShareContactRequestDto.toShareContactRequest(user.getId());
    shareContactRequestRepository.save(shareContactRequest);

    // Return response indicating the request was processed
    return ExpectShareContactRequestResponse.of();
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
   * @throws UnableToCompleteOperationException if the request is invalid or cannot be processed
   */
  @Override
  public ProcessShareContactRequestResponse processShareContactRequest(final Long shareContactRequestId, final ProcessShareContactRequestDto processShareContactRequestDto, final FleenUser user) {
    // Validate that the user exists
    memberRepository.findById(user.getId())
      .orElseThrow(() -> new UserNotFoundException(user.getId()));

    // Retrieve the share contact request by its ID
    final ShareContactRequest shareContactRequest = shareContactRequestRepository.findById(shareContactRequestId)
      .orElseThrow(() -> new ShareContactRequestNotFoundException(shareContactRequestId));

    // Extract contact information from the DTO
    final String contact = processShareContactRequestDto.getContact();
    // Verify the recipient of the share contact request
    verifyRecipient(shareContactRequest.getRecipient(), user.toMember());
    // Extract and verify the request status and contact details
    final ShareContactRequestStatus shareContactRequestStatus = processShareContactRequestDto.getActualShareContactRequestStatus();
    // Ensure the request is to be accepted and if the contact to be shared is valid
    verifyContactRequestStatusAndContact(shareContactRequestStatus, contact);
    // Ensure that the request has not been accepted or rejected already
    verifyRequestHasNotBeenAcceptedOrRejected(shareContactRequestStatus);
    // Ensure that the request can only be accepted or rejected
    verifyShareContactRequestCanOnlyBeAcceptedOrRejected(shareContactRequestStatus);

    // Update the share contact request with new details
    shareContactRequest.update(
      processShareContactRequestDto.getActualShareContactRequestStatus(),
      processShareContactRequestDto.getActualContactType(),
      contact,
      processShareContactRequestDto.getComment());

    // Save the updated share contact request
    shareContactRequestRepository.save(shareContactRequest);
    // Return response indicating successful processing of the share contact request
    return ProcessShareContactRequestResponse.of();
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
  public SendShareContactRequestResponse sendShareContactRequest(final SendShareContactRequestDto sendShareContactRequestDto, final FleenUser user) {
    // Validate that the recipient user exists
    memberRepository.findById(sendShareContactRequestDto.getRecipientId())
      .orElseThrow(() -> new UserNotFoundException(sendShareContactRequestDto.getRecipientId()));

    // Convert DTO to ShareContactRequest and save it
    final ShareContactRequest shareContactRequest = sendShareContactRequestDto.toShareContactRequest(user.getId());
    shareContactRequestRepository.save(shareContactRequest);

    // Return response indicating successful creation of the share contact request
    return SendShareContactRequestResponse.of();
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
  public CancelShareContactRequestResponse cancelShareContactRequest(final Long shareContactRequestId, final FleenUser user) {
    // Retrieve the share contact request based on ID and user
    final ShareContactRequest shareContactRequest = shareContactRequestRepository.findByShareContactRequestIdAndInitiator(shareContactRequestId, user.toMember())
      .orElseThrow(() -> new ShareContactRequestNotFoundException(shareContactRequestId));

    // Check if the request is already canceled
    if (ShareContactRequestStatus.isCanceled(shareContactRequest.getShareContactRequestStatus())) {
      throw new ShareContactRequestAlreadyCanceledException();
    }
    // Verify if the request has been accepted or rejected
    verifyRequestHasBeenAcceptedOrRejected(shareContactRequest.getShareContactRequestStatus());

    // Set the request status to CANCELED and save it
    shareContactRequest.setShareContactRequestStatus(ShareContactRequestStatus.CANCELED);
    shareContactRequestRepository.save(shareContactRequest);

    // Return response indicating successful cancellation
    return CancelShareContactRequestResponse.of();
  }

  /**
   * Verifies the contact request status and contact information.
   * If the status is `ACCEPTED`, verifies that the contact is not null.
   *
   * @param shareContactRequestStatus the status of the contact request
   * @param contact the contact information to verify if the status is `ACCEPTED`
   * @throws UnableToCompleteOperationException if the status is null
   * @throws ShareContactRequestValueRequiredException if the status is `ACCEPTED` and the contact is null
   */
  protected void verifyContactRequestStatusAndContact(final ShareContactRequestStatus shareContactRequestStatus, final String contact) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, UnableToCompleteOperationException::new);

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
   * If either the recipient or the current user is null, an `UnableToCompleteOperationException` is thrown.
   * If the recipient's ID does not match the current user's ID, a `CannotProcessShareContactRequestException` is thrown.
   *
   * @param recipient the intended recipient of the contact sharing request
   * @param currentUser the user initiating the request
   * @throws UnableToCompleteOperationException if either the recipient or the current user is null
   * @throws CannotProcessShareContactRequestException if the recipient's ID does not match the current user's ID
   */
  protected void verifyRecipient(final Member recipient, final Member currentUser) {
    // Throw an exception if the any of the provided values is null
    checkIsNullAny(Set.of(recipient, currentUser), UnableToCompleteOperationException::new);

    // Check if the recipient's ID matches the current user's ID
    final boolean isSame = Objects.equals(recipient.getMemberId(), currentUser.getMemberId());
    if (!isSame) {
      throw new CannotProcessShareContactRequestException();
    }
  }

  /**
   * Verifies if a contact sharing request has not been confirmed or rejected.
   * If the request status is null, an `UnableToCompleteOperationException` is thrown.
   * If the request has not been confirmed or rejected, a `ShareContactRequestAlreadyProcessedException` is thrown.
   *
   * @param shareContactRequestStatus the status of the contact sharing request to verify
   * @throws UnableToCompleteOperationException if the status is null
   * @throws ShareContactRequestAlreadyProcessedException if the request has not been confirmed or rejected
   */
  protected void verifyRequestHasNotBeenAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, UnableToCompleteOperationException::new);

    // Check if the request has not been confirmed or rejected
    if (!(ShareContactRequestStatus.isAcceptedOrRejected(shareContactRequestStatus))) {
      throw new ShareContactRequestAlreadyProcessedException();
    }
  }

  /**
   * Verifies if a contact sharing request has been confirmed or rejected.
   * If the request status is null, an `UnableToCompleteOperationException` is thrown.
   * If the request has already been confirmed or rejected, a `CannotCancelShareContactRequestException` is thrown.
   *
   * @param shareContactRequestStatus the status of the contact sharing request to verify
   * @throws UnableToCompleteOperationException if the status is null
   * @throws CannotCancelShareContactRequestException if the request has already been confirmed or rejected
   */
  protected void verifyRequestHasBeenAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, UnableToCompleteOperationException::new);

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
   * @throws UnableToCompleteOperationException if the status is null
   * @throws CannotProcessShareContactRequestException if the status is SENT or CANCELED
   */
  protected void verifyShareContactRequestCanOnlyBeAcceptedOrRejected(final ShareContactRequestStatus shareContactRequestStatus) {
    // Throw an exception if the provided share contact request status is null
    checkIsNull(shareContactRequestStatus, UnableToCompleteOperationException::new);

    // Check if the status is either SENT or CANCELED, which are invalid for acceptance or rejection
    if (ShareContactRequestStatus.isSentOrCanceled(shareContactRequestStatus)) {
      throw new CannotProcessShareContactRequestException();
    }
  }

}
