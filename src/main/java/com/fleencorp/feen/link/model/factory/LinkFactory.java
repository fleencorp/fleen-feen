package com.fleencorp.feen.link.model.factory;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.model.domain.Link;
import com.fleencorp.feen.link.model.holder.LinkParentDetailHolder;
import com.fleencorp.feen.user.model.domain.Member;

import java.util.Map;
import java.util.function.Consumer;

import static com.fleencorp.feen.link.model.dto.UpdateLinkDto.LinkDto;
import static java.util.Objects.isNull;

public final class LinkFactory {

  private LinkFactory() {}

  private static final Map<LinkParentType, LinkCreator> CREATORS = Map.of(
    LinkParentType.BUSINESS, LinkFactory::createBusinessLink,
    LinkParentType.CHAT_SPACE, LinkFactory::createChatSpaceLink,
    LinkParentType.STREAM, LinkFactory::createStreamLink,
    LinkParentType.USER, LinkFactory::createUserLink
  );

  public static Link by(final LinkDto dto, final LinkParentDetailHolder detailsHolder, final Member member) {
    final LinkParentType linkParentType = detailsHolder.parentType();
    final LinkCreator creator = CREATORS.get(linkParentType);

    if (isNull(creator)) {
      throw FailedOperationException.of();
    }

    return creator.create(dto, detailsHolder, member);
  }

  private static Link createBusinessLink(final LinkDto dto, final LinkParentDetailHolder holder, final Member member) {
    final Business business = holder.business();

    return createBaseLink(dto, business.getBusinessId(), LinkParentType.BUSINESS, member)
      .apply(link -> {
        link.setBusiness(business);
        link.setBusinessId(business.getBusinessId());
    });
  }

  private static Link createChatSpaceLink(final LinkDto dto, final LinkParentDetailHolder holder, final Member member) {
    final ChatSpace chatSpace = holder.chatSpace();
    return createBaseLink(dto, chatSpace.getChatSpaceId(), LinkParentType.CHAT_SPACE, member)
      .apply(link -> {
        link.setChatSpace(chatSpace);
        link.setChatSpaceId(chatSpace.getChatSpaceId());
    });
  }

  private static Link createStreamLink(final LinkDto dto, final LinkParentDetailHolder holder, final Member member) {
    return createBaseLink(dto, null, LinkParentType.STREAM, member)
      .apply(_ -> {});
  }

  private static Link createUserLink(final LinkDto dto, final LinkParentDetailHolder holder, final Member member) {
    return createBaseLink(dto, null, LinkParentType.USER, member)
      .apply(_ -> {});
  }

  private static LinkApplier createBaseLink(final LinkDto dto, final Long parentId, final LinkParentType parentType, final Member member) {
    final Link link = new Link();
    link.setParentId(parentId);
    link.setLinkParentType(parentType);
    link.setUrl(dto.getUrl());
    link.setLinkType(dto.getLinkType());

    link.setMemberId(member.getMemberId());
    link.setMember(member);
    return new LinkApplier(link);
  }

  @FunctionalInterface
  private interface LinkCreator {
    Link create(LinkDto dto, LinkParentDetailHolder holder, Member member);
  }

  private record LinkApplier(Link link) {
    public Link apply(final Consumer<Link> configurer) {
      configurer.accept(link);
      return link;
    }
  }
}

