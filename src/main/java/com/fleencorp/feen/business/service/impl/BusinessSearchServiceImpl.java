package com.fleencorp.feen.business.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.business.exception.BusinessNotFoundException;
import com.fleencorp.feen.business.mapper.BusinessMapper;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.model.request.search.BusinessSearchRequest;
import com.fleencorp.feen.business.model.response.core.BusinessResponse;
import com.fleencorp.feen.business.model.search.BusinessSearchResult;
import com.fleencorp.feen.business.repository.BusinessRepository;
import com.fleencorp.feen.business.service.BusinessSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.common.service.impl.misc.MiscServiceImpl.setEntityUpdatableByUser;

@Service
public class BusinessSearchServiceImpl implements BusinessSearchService {

  private final BusinessRepository businessRepository;
  private final BusinessMapper businessMapper;
  private final Localizer localizer;

  public BusinessSearchServiceImpl(
    final BusinessRepository businessRepository,
    final BusinessMapper businessMapper,
    final Localizer localizer) {
    this.businessRepository = businessRepository;
    this.businessMapper = businessMapper;
    this.localizer = localizer;
  }

  @Override
  public BusinessSearchResult findBusinesses(final BusinessSearchRequest searchRequest, final RegisteredUser user) {
    final Page<Business> page;

    final Pageable pageable = searchRequest.getPage();
    final String title = searchRequest.getTitle();

    if (searchRequest.hasTitle()) {
      page = businessRepository.findByTitleContainingIgnoreCase(title, searchRequest.getStatus(), pageable);
    } else {
      page = businessRepository.findMany(searchRequest.getStatus(), pageable);
    }

    final Collection<BusinessResponse> businessResponses = businessMapper.toBusinessResponses(page.getContent());
    businessResponses.forEach(businessResponse -> setEntityUpdatableByUser(businessResponse, user.getId()));
    final SearchResult searchResult = toSearchResult(businessResponses, page);

    final BusinessSearchResult businessSearchResult = BusinessSearchResult.of(searchResult);
    return localizer.of(businessSearchResult);
  }

  @Override
  public Business findBusiness(final Long businessId) {
    return businessRepository.findById(businessId)
      .orElseThrow(BusinessNotFoundException.of(businessId));
  }

  @Override
  public Business findBusinessAndVerifyOwnerRequired(final Long businessId, final Member member) {
    final Business business = findBusiness(businessId);
    business.verifyIsOwner(member.getMemberId());

    return business;
  }
}
