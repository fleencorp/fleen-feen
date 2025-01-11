package com.fleencorp.feen.mapper.impl.user;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.mapper.user.UserMapper;
import com.fleencorp.feen.model.info.user.ProfileStatusInfo;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
public class UserMapperImpl implements UserMapper {

  private final MessageSource messageSource;

  /**
   * Constructs a new instance of {@code UserMapperImpl} with the specified {@code MessageSource}.
   *
   * <p>The {@code MessageSource} is used to retrieve localized messages for mapping user-related
   * data, enabling internationalization support within the application.</p>
   *
   * @param messageSource the message source used for retrieving localized messages
   */
  public UserMapperImpl(
      final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  /**
   * Translates the provided message code into a localized message based on the current locale.
   *
   * <p>This method retrieves the current locale from the {@link LocaleContextHolder}, and then uses the
   * {@link MessageSource} to resolve the message corresponding to the provided {@code messageCode}.
   * The method returns the translated message string for the current locale. If the message code cannot be found,
   * the method may return the default message or throw an exception based on the configuration of the {@link MessageSource}.</p>
   *
   * @param messageCode The code representing the message to be translated.
   * @return The localized message corresponding to the {@code messageCode} for the current locale.
   */
  private String translate(final String messageCode) {
    final Locale locale = LocaleContextHolder.getLocale();
    return messageSource.getMessage(messageCode, null, locale);
  }


  /**
   * Converts a {@link ProfileStatus} object to a {@link ProfileStatusInfo} object.
   *
   * @param profileStatus the {@link ProfileStatus} object to convert
   * @return a {@link ProfileStatusInfo} object containing the status information
   *         and a translated message based on the {@code profileStatus.getMessageCode()}
   */
  @Override
  public ProfileStatusInfo toProfileStatusInfo(ProfileStatus profileStatus) {
    return ProfileStatusInfo.of(profileStatus, translate(profileStatus.getMessageCode()));
  }
}
