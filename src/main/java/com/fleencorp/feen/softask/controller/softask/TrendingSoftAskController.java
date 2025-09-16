package com.fleencorp.feen.softask.controller.softask;

import com.fleencorp.base.resolver.SearchParam;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.request.SoftAskSearchRequest;
import com.fleencorp.feen.softask.model.search.SoftAskSearchResult;
import com.fleencorp.feen.softask.service.softask.TrendingSoftAskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/soft-ask")
public class TrendingSoftAskController {

  private final TrendingSoftAskService trendingSoftAskService;

  public TrendingSoftAskController(final TrendingSoftAskService trendingSoftAskService) {
    this.trendingSoftAskService = trendingSoftAskService;
  }

  @GetMapping(value = "/trending")
  public SoftAskSearchResult trendingSoftAsk(
    @SearchParam final SoftAskSearchRequest request,
    @AuthenticationPrincipal final RegisteredUser user) {
    return trendingSoftAskService.trendingSoftAsks(request, user);
  }
}
