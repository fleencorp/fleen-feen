package com.fleencorp.feen.business.service.impl;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.repository.BusinessRepository;
import com.fleencorp.feen.business.service.BusinessOperationService;
import com.fleencorp.feen.business.service.BusinessSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import org.springframework.stereotype.Service;

@Service
public class BusinessOperationServiceImpl implements BusinessOperationService {

  private final BusinessSearchService businessSearchService;
  private final BusinessRepository businessRepository;

  public BusinessOperationServiceImpl(
    final BusinessSearchService businessSearchService,
    final BusinessRepository businessRepository) {
    this.businessSearchService = businessSearchService;
    this.businessRepository = businessRepository;
  }

  @Override
  public Business save(final Business business) {
    return businessRepository.save(business);
  }

  @Override
  public Business findBusiness(final Long businessId) {
    return businessSearchService.findBusiness(businessId);
  }

  @Override
  public Business findBusinessAndVerifyOwner(final Long businessId, final Member member) {
    return businessSearchService.findBusinessAndVerifyOwnerRequired(businessId, member);
  }
}
