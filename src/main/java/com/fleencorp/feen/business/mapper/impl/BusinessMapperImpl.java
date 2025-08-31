package com.fleencorp.feen.business.mapper.impl;

import com.fleencorp.feen.business.constant.BusinessChannelType;
import com.fleencorp.feen.business.constant.BusinessStatus;
import com.fleencorp.feen.business.mapper.BusinessMapper;
import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.business.model.info.BusinessChannelTypeInfo;
import com.fleencorp.feen.business.model.info.BusinessStatusInfo;
import com.fleencorp.feen.business.model.response.core.BusinessResponse;
import com.fleencorp.feen.common.model.info.ShareCountInfo;
import com.fleencorp.feen.link.mapper.LinkMapper;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.response.base.LinkResponse;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Objects.nonNull;

@Component
public class BusinessMapperImpl extends BaseMapper implements BusinessMapper {

  private final LinkMapper linkMapper;
  private final ToInfoMapper toInfoMapper;

  public BusinessMapperImpl(
      final LinkMapper linkMapper,
      final ToInfoMapper toInfoMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.linkMapper = linkMapper;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Converts a {@link Business} entity into a {@link BusinessResponse} object.
   *
   * <p>This method maps all relevant fields from the given {@code Business} entity
   * into a new {@code BusinessResponse} instance. It includes identifiers, descriptive
   * details, ownership information, channel type, status, and timestamps. In addition,
   * the response is configured with default values, such as setting
   * {@code isUpdatable} to {@code false}.</p>
   *
   * @param entry the {@link Business} entity to be converted; may be {@code null}
   * @return a {@link BusinessResponse} populated with the mapped values from the given
   *         {@code Business}, or {@code null} if the input is {@code null}
   */
  @Override
  public BusinessResponse toBusinessResponse(final Business entry) {
    if (nonNull(entry)) {
      final BusinessResponse response = new BusinessResponse();
      response.setId(entry.getBusinessId());
      response.setTitle(entry.getTitle());
      response.setMotto(entry.getMotto());
      response.setDescription(entry.getDescription());
      response.setOtherDetails(entry.getOtherDetails());

      response.setFoundingYear(entry.getFoundingYear());
      response.setAddress(entry.getAddress());
      response.setCountry(entry.getCountry());

      response.setAuthorId(entry.getOwnerId());
      response.setOrganizerId(entry.getOwnerId());

      response.setUpdatedOn(entry.getUpdatedOn());
      response.setCreatedOn(entry.getCreatedOn());
      response.setSlug(entry.getSlug());
      response.setIsUpdatable(false);

      final BusinessChannelType channelType = entry.getChannelType();
      final BusinessChannelTypeInfo channelTypeInfo = toBusinessChannelTypeInfo(channelType);
      response.setChannelTypeInfo(channelTypeInfo);

      final BusinessStatus status = entry.getStatus();
      final BusinessStatusInfo businessStatusInfo = toBusinessStatusInfo(status);
      response.setStatusInfo(businessStatusInfo);

      final ShareCountInfo shareCountInfo = toInfoMapper.toShareCountInfo(entry.getShareCount());
      response.setShareCountInfo(shareCountInfo);

      final Set<Link> links = entry.getLinks();
      final Collection<LinkResponse> linkResponses = linkMapper.toLinkResponses(new ArrayList<>(links));
      response.setLinks(linkResponses);

      return response;
    }

    return null;
  }

  /**
   * Converts a collection of {@link Business} entities into a collection of {@link BusinessResponse} objects.
   *
   * <p>This method safely handles {@code null} or empty input by returning an empty collection.
   * Each non-null {@link Business} entity in the provided collection is mapped to a
   * corresponding {@link BusinessResponse} using {@link #toBusinessResponse(Business)}.</p>
   *
   * @param entries the collection of {@link Business} entities to be converted; may be {@code null}
   * @return a collection of {@link BusinessResponse} objects corresponding to the input entities,
   *         or an empty collection if the input is {@code null} or contains no valid entities
   */
  @Override
  public Collection<BusinessResponse> toBusinessResponses(final Collection<Business> entries) {
    return Optional.ofNullable(entries)
      .orElseGet(Collections::emptyList)
      .stream()
      .filter(Objects::nonNull)
      .map(this::toBusinessResponse)
      .toList();
  }

  private BusinessChannelTypeInfo toBusinessChannelTypeInfo(final BusinessChannelType channelType) {
    return BusinessChannelTypeInfo.of(channelType, translate(channelType.getMessageCode()), translate(channelType.getMessageCode2()));
  }

  private BusinessStatusInfo toBusinessStatusInfo(final BusinessStatus status) {
    return BusinessStatusInfo.of(status, translate(status.getMessageCode()), translate(status.getMessageCode2()));
  }

}
