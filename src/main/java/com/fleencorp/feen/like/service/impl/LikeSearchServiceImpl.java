package com.fleencorp.feen.like.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.like.constant.LikeParentType;
import com.fleencorp.feen.like.constant.LikeType;
import com.fleencorp.feen.like.mapper.LikeMapper;
import com.fleencorp.feen.like.model.domain.Like;
import com.fleencorp.feen.like.model.request.search.LikeSearchRequest;
import com.fleencorp.feen.like.model.response.LikeResponse;
import com.fleencorp.feen.like.model.search.LikeSearchResult;
import com.fleencorp.feen.like.repository.LikeSearchRepository;
import com.fleencorp.feen.like.service.LikeSearchService;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.ExceptionUtil.checkIsNull;
import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static java.util.Objects.nonNull;

@Service
public class LikeSearchServiceImpl implements LikeSearchService {

  private final LikeSearchRepository likeSearchRepository;
  private final LikeMapper likeMapper;
  private final Localizer localizer;

  public LikeSearchServiceImpl(
      final LikeSearchRepository likeSearchRepository,
      final LikeMapper likeMapper,
      final Localizer localizer) {
    this.likeSearchRepository = likeSearchRepository;
    this.likeMapper = likeMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves likes based on the given {@link LikeSearchRequest} for the specified {@link RegisteredUser}.
   *
   * <p>This method delegates to {@link #searchLikes(LikeSearchRequest, RegisteredUser)} to perform
   * the search query, then maps the results into {@link LikeResponse} objects. The responses are
   * wrapped in a {@link SearchResult} and further encapsulated in a {@link LikeSearchResult}.
   * Finally, the result is localized using the {@code localizer}.</p>
   *
   * @param searchRequest the {@link LikeSearchRequest} containing pagination, filters,
   *                      and other search criteria
   * @param user the {@link RegisteredUser} for whom the likes are being retrieved
   * @return a localized {@link LikeSearchResult} containing the paginated like results
   * @throws FailedOperationException if the user's member association is {@code null}
   */
  @Override
  @Transactional(readOnly = true)
  public LikeSearchResult findLikes(final LikeSearchRequest searchRequest, final RegisteredUser user) {
    final Page<Like> page = searchLikes(searchRequest, user);

    final Collection<LikeResponse> likeResponses = likeMapper.toLikeResponses(page.getContent());
    final SearchResult searchResult = toSearchResult(likeResponses, page);
    final LikeSearchResult likeSearchResult = LikeSearchResult.of(searchResult);

    return localizer.of(likeSearchResult);
  }

  /**
   * Searches for {@link Like} entities based on the provided {@link LikeSearchRequest}
   * and the given {@link RegisteredUser}.
   *
   * <p>This method applies different filtering criteria depending on the search request.
   * If both the start and end dates are set, results are restricted to likes created
   * within that date range. If a title is specified, results are filtered by title.
   * Otherwise, the query retrieves likes across the specified parent types without
   * additional filtering. Only entries of type {@link LikeType#LIKE} are considered
   * in the results.</p>
   *
   * <p>If the {@link RegisteredUser} does not have an associated {@link Member},
   * a {@link FailedOperationException} is thrown.</p>
   *
   * @param searchRequest the {@link LikeSearchRequest} containing pagination, date range,
   *                      title, and parent type filters
   * @param user the {@link RegisteredUser} whose like data is being retrieved
   * @return a {@link Page} of {@link Like} results that match the search criteria
   * @throws FailedOperationException if the user's member association is {@code null}
   */
  private Page<Like> searchLikes(final LikeSearchRequest searchRequest, final RegisteredUser user) {
    checkIsNull(user.toMember(), FailedOperationException::new);

    final Page<Like> page;
    final Pageable pageable = searchRequest.getPage();
    final LocalDateTime endDate = searchRequest.getEndDateTime();
    final LocalDateTime startDate = searchRequest.getStartDateTime();
    final String title = searchRequest.getTitle();
    final List<LikeParentType> likeParentTypes = searchRequest.getLikeParentType();
    final LikeType likeType = LikeType.LIKE;
    final Long memberId = user.getId();

    if (searchRequest.areAllDatesSet()) {
      page = likeSearchRepository.findByDateBetween(startDate, endDate, likeParentTypes, likeType, memberId, pageable);
    } else if (nonNull(title))  {
      page = likeSearchRepository.findByTitle(title, likeParentTypes, likeType, memberId, pageable);
    } else {
      page = likeSearchRepository.findMany(likeParentTypes, likeType, memberId, pageable);
    }

    return page;
  }
}
