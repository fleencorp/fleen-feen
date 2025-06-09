package com.fleencorp.feen.user.mapper.impl;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.user.mapper.UserMapper;
import com.fleencorp.feen.user.model.info.ProfileStatusInfo;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@code UserMapper} interface.
 *
 * <p>This class provides functionality for mapping user-related data, including
 * the transformation of user entities or DTOs into other formats or models.
 * It also supports internationalization by utilizing a {@link MessageSource}
 * for retrieving localized messages.</p>
 *
 * @author Yusuf Àlàmú Musa
 * @version 1.0
 */
@Component
public class UserMapperImpl extends BaseMapper implements UserMapper {

  public UserMapperImpl(final MessageSource messageSource) {
    super(messageSource);
  }

  /**
   * Converts a {@link ProfileStatus} object to a {@link ProfileStatusInfo} object.
   *
   * @param profileStatus the {@link ProfileStatus} object to convert
   * @return a {@link ProfileStatusInfo} object containing the status information
   *         and a translated message based on the {@code profileStatus.getMessageCode()}
   */
  @Override
  public ProfileStatusInfo toProfileStatusInfo(final ProfileStatus profileStatus) {
    return ProfileStatusInfo.of(profileStatus, translate(profileStatus.getMessageCode()));
  }
}
