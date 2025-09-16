package com.fleencorp.feen.softask.service.impl.softask;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.holder.UserOtherDetailHolder;
import com.fleencorp.feen.softask.model.projection.SoftAskWithDetail;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.repository.softask.TrendingSoftAskRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.softask.TrendingSoftAskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TrendingSoftAskServiceImpl implements TrendingSoftAskService {

  private final SoftAskCommonService softAskCommonService;
  private final TrendingSoftAskRepository trendingSoftAskRepository;

  public TrendingSoftAskServiceImpl(
      final SoftAskCommonService softAskCommonService,
      final TrendingSoftAskRepository trendingSoftAskRepository) {
    this.softAskCommonService = softAskCommonService;
    this.trendingSoftAskRepository = trendingSoftAskRepository;
  }

  /**
   * Retrieves a paginated list of trending soft asks near the location provided
   * in the search request. The search uses the given latitude and longitude with
   * a default radius to find nearby trending soft asks. The result is then processed
   * to include user-specific context such as membership details and other user
   * information before being returned.
   *
   * @param searchRequest the search request containing location, pagination,
   *                      and user context details
   * @param user the registered user making the request, used to resolve membership
   * @return a processed search result containing trending soft asks with
   *         enriched details
   */
  @Override
  public SoftAskSearchResult trendingSoftAsks(SoftAskSearchRequest searchRequest, RegisteredUser user) {
    final IsAMember member = user.toMember();
    final Pageable pageable = searchRequest.getPage();
    final UserOtherDetailHolder userOtherDetailHolder = searchRequest.getUserOtherDetail();
    final Double latitude = searchRequest.getLatitude();
    final Double longitude = searchRequest.getLongitude();
    final Double defaultRadius = 5000.0;

    final Page<SoftAskWithDetail> page = trendingSoftAskRepository.findTrendingNearby(latitude, longitude, defaultRadius, pageable);
    return softAskCommonService.processAndReturnSoftAsks(page, member, userOtherDetailHolder);
  }
}
