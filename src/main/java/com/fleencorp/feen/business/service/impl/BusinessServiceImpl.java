package com.fleencorp.feen.business.service.impl;

import com.fleencorp.feen.business.exception.BusinessNotFoundException;
import com.fleencorp.feen.business.exception.BusinessNotOwnerException;
import com.fleencorp.feen.business.mapper.BusinessMapper;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.model.dto.AddBusinessDto;
import com.fleencorp.feen.business.model.dto.DeleteBusinessDto;
import com.fleencorp.feen.business.model.dto.UpdateBusinessDto;
import com.fleencorp.feen.business.model.response.BusinessAddResponse;
import com.fleencorp.feen.business.model.response.BusinessDeleteResponse;
import com.fleencorp.feen.business.model.response.BusinessUpdateResponse;
import com.fleencorp.feen.business.model.response.core.BusinessResponse;
import com.fleencorp.feen.business.repository.BusinessRepository;
import com.fleencorp.feen.business.service.BusinessSearchService;
import com.fleencorp.feen.business.service.BusinessService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BusinessServiceImpl implements BusinessService {

  private final BusinessSearchService businessSearchService;
  private final BusinessRepository businessRepository;
  private final BusinessMapper businessMapper;
  private final Localizer localizer;

  public BusinessServiceImpl(
      final BusinessSearchService businessSearchService,
      final BusinessRepository businessRepository,
      final BusinessMapper businessMapper,
      final Localizer localizer) {
    this.businessSearchService = businessSearchService;
    this.businessRepository = businessRepository;
    this.businessMapper = businessMapper;
    this.localizer = localizer;
  }

  /**
   * Creates a new business owned by the specified user.
   *
   * <p>This method converts the provided {@link AddBusinessDto} into a {@link Business} entity,
   * associates it with the given {@link RegisteredUser}, persists it, and returns a localized
   * {@link BusinessAddResponse} containing the details of the newly created business.</p>
   *
   * @param addBusinessDto the DTO containing the details of the business to be created
   * @param user the {@link RegisteredUser} who is creating and owning the business
   * @return a localized {@link BusinessAddResponse} containing the details of the newly created business
   */
  @Override
  public BusinessAddResponse addBusiness(final AddBusinessDto addBusinessDto, final RegisteredUser user) {
    Business business = addBusinessDto.toBusiness(user.toMember());
    business = businessRepository.save(business);

    final BusinessResponse businessResponse = businessMapper.toBusinessResponse(business);
    final BusinessAddResponse businessAddResponse = BusinessAddResponse.of(business.getBusinessId(), businessResponse);

    return localizer.of(businessAddResponse);
  }

  /**
   * Updates the details of a business owned by the specified user.
   *
   * <p>This method retrieves the {@link Business} entity by its ID, verifies ownership
   * against the provided {@link RegisteredUser}, applies the updates from the
   * {@link UpdateBusinessDto}, persists the changes, and returns a localized
   * {@link BusinessUpdateResponse} containing the updated business details.</p>
   *
   * @param businessId the ID of the {@link Business} to be updated
   * @param updateBusinessDto the DTO containing the updated business details
   * @param user the {@link RegisteredUser} requesting the update; must be the owner of the business
   * @return a localized {@link BusinessUpdateResponse} containing the updated business details
   * @throws BusinessOwnershipException if the specified user is not the owner of the business
   * @throws BusinessNotFoundException if no business exists with the given ID
   */
  @Override
  @Transactional
  public BusinessUpdateResponse updateBusiness(final Long businessId, final UpdateBusinessDto updateBusinessDto, final RegisteredUser user)
    throws BusinessNotFoundException, BusinessNotOwnerException {
    final Business business = businessSearchService.findBusiness(businessId);
    business.checkIsOwner(user.getId());

    business.update(
      updateBusinessDto.getTitle(),
      updateBusinessDto.getMotto(),
      updateBusinessDto.getDescription(),
      updateBusinessDto.getOtherDetails(),
      updateBusinessDto.getFoundingYear(),
      updateBusinessDto.getBusinessStatus()
    );

    businessRepository.save(business);
    final BusinessResponse businessResponse = businessMapper.toBusinessResponse(business);
    final BusinessUpdateResponse businessUpdateResponse = BusinessUpdateResponse.of(businessId, businessResponse);

    return localizer.of(businessUpdateResponse);
  }

  /**
   * Deletes a business owned by the specified user.
   *
   * <p>This method retrieves the {@link Business} entity by its ID from the provided
   * {@link DeleteBusinessDto}, verifies that the given {@link RegisteredUser} is the owner,
   * performs the deletion, persists the change, and returns a localized
   * {@link BusinessDeleteResponse} confirming the deletion.</p>
   *
   * @param deleteBusinessDto the DTO containing the ID of the {@link Business} to be deleted
   * @param user the {@link RegisteredUser} requesting the deletion; must be the owner of the business
   * @return a localized {@link BusinessDeleteResponse} confirming the deletion of the business
   * @throws BusinessOwnershipException if the specified user is not the owner of the business
   * @throws BusinessNotFoundException if no business exists with the given ID
   */
  @Override
  @Transactional
  public BusinessDeleteResponse deleteBusiness(final DeleteBusinessDto deleteBusinessDto, final RegisteredUser user)
    throws BusinessNotFoundException, BusinessNotOwnerException {
    final Long businessId = deleteBusinessDto.getBusinessId();
    final Business business = businessSearchService.findBusiness(businessId);
    business.checkIsOwner(user.getId());

    business.delete();
    businessRepository.save(business);
    final BusinessDeleteResponse businessDeleteResponse = BusinessDeleteResponse.of(businessId);

    return localizer.of(businessDeleteResponse);
  }
}
