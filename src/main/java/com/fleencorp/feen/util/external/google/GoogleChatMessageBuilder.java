package com.fleencorp.feen.util.external.google;

import com.fleencorp.feen.model.request.chat.space.message.GoogleChatSpaceMessageRequest;
import com.google.apps.card.v1.*;
import com.google.chat.v1.AccessoryWidget;
import com.google.chat.v1.CardWithId;
import com.google.chat.v1.Message;
import com.google.protobuf.FloatValue;
import com.google.type.Color;
import lombok.extern.slf4j.Slf4j;

/**
 * Builder class for creating Google Chat messages.
 *
 * <p>This class provides methods to construct and customize messages to be sent in
 * Google Chat. It allows the setting of various components such as titles, cards,
 * sections, and widgets, facilitating the creation of rich interactive messages.</p>
 *
 * @see <a href="https://developers.google.com/workspace/chat/format-messages">
 *   Format messages</a>
 * @see <a href="https://developers.google.com/workspace/chat/create-messages">
 *   Send a message using the Google Chat API</a>
 * @see <a href="https://addons.gsuite.google.com/uikit/builder">
 *   Card Builder UI</a>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Slf4j
public final class GoogleChatMessageBuilder {

  private GoogleChatMessageBuilder() {}

  /**
   * Creates a new {@link Message.Builder} instance with the specified title and card.
   *
   * <p>This method initializes a {@link Message.Builder} by setting the message text to the provided title
   * and adding a card defined by the {@link CardWithId.Builder} instance. The returned builder can be
   * further customized before building the final {@link Message} object.</p>
   *
   * <p>Accessory widgets are used over CardFixedFooter button because this feature is not available
   * in Cards.</p>
   *
   * @param title      the title text for the message
   * @param cardWithId the {@link CardWithId.Builder} instance to be added to the message
   * @return a {@link Message.Builder} configured with the specified title and card
   *
   * @see <a href="https://developers.google.com/workspace/chat/api/reference/rest/v1/cards#Message.CardFixedFooter">
   *   CardFixedFooter</a>
   * @see <a href="https://developers.google.com/workspace/chat/design-components-card-dialog#add_a_persistent_footer">
   *   Add a persistent footer</a>
   */
  public static Message.Builder ofMessage(final String title, final CardWithId.Builder cardWithId, final GoogleChatSpaceMessageRequest chatSpaceMessageRequest) {
    return Message.newBuilder()
      .setText(title)
      .addCardsV2(cardWithId)
      .addAccessoryWidgets(ofAccessoryWidget()
        .setButtonList(ofButtonList()
          .addButtons(ofButton(chatSpaceMessageRequest))));
  }

  /**
   * Creates a new {@link CardWithId.Builder} instance from the specified chat space message request.
   *
   * <p>This method initializes a {@link CardWithId.Builder} by setting the card to a new instance
   * created from the provided {@link GoogleChatSpaceMessageRequest}. The resulting builder can
   * be further customized before building the final {@link CardWithId} object.</p>
   *
   * @param chatSpaceMessageRequest the {@link GoogleChatSpaceMessageRequest} used to create the card
   * @return a {@link CardWithId.Builder} configured with the specified card
   */
  public static CardWithId.Builder ofCardWithId(final GoogleChatSpaceMessageRequest chatSpaceMessageRequest) {
    return CardWithId.newBuilder()
      .setCard(
        ofCard(chatSpaceMessageRequest)
      );
  }

  /**
   * Creates a new {@link Card.Builder} instance from the specified chat space message request.
   *
   * <p>This method initializes a {@link Card.Builder} by setting the header, sections, and footer
   * based on the provided {@link GoogleChatSpaceMessageRequest}. The resulting builder can be
   * further customized before building the final {@link Card} object.</p>
   *
   * @param request the {@link GoogleChatSpaceMessageRequest} used to configure the card
   * @return a {@link Card.Builder} configured with the specified request's details
   */
  public static Card.Builder ofCard(final GoogleChatSpaceMessageRequest request) {
    return Card.newBuilder()
      .setHeader(ofCardHeader(request))
      .addSections(ofCardSection()
        .setHeader(request.getSectionHeader())
        .addWidgets(ofWidget().setDivider(ofDivider()))
        .setCollapsible(false)
        .setUncollapsibleWidgetsCount(request.getUnCollapsibleWidgetsCount())
        .addWidgets(ofWidget().setDecoratedText(ofDecoratedText(request.getFirstDecoratedText())))
        .addWidgets(ofWidget().setDecoratedText(ofDecoratedText(request.getSecondDecoratedText())))
      );
  }

  /**
   * Creates a new builder for a Card Section.
   *
   * <p>This method returns a new instance of the {@code Card.Section.Builder},
   * which is used to build sections within a Google Chat card. Each card section
   * can contain multiple widgets, such as text, images, buttons, or other elements,
   * that allow for richer content within chat messages.</p>
   *
   * @return a new {@code Card.Section.Builder} instance for constructing a section of a card.
   */
  public static Card.Section.Builder ofCardSection() {
    return Card.Section.newBuilder();
  }

  /**
   * Creates a new {@link Widget.Builder} instance for constructing a widget.
   *
   * <p>This method initializes a {@link Widget.Builder}, which can be used to configure
   * various properties of a widget in a chat card. The resulting builder allows the
   * addition of specific widget features before building the final {@link Widget} object.</p>
   *
   * @return a {@link Widget.Builder} instance for configuring a widget
   */
  public static Widget.Builder ofWidget() {
    return Widget.newBuilder();
  }

  /**
   * Creates a new {@link Card.CardHeader.Builder} instance for constructing a card header.
   *
   * <p>This method initializes a {@link Card.CardHeader.Builder} with properties
   * derived from the given {@link GoogleChatSpaceMessageRequest}. The resulting builder
   * allows for the configuration of the card header, including the title, subtitle,
   * image URL, and image type before building the final {@link Card.CardHeader} object.</p>
   *
   * @param request the {@link GoogleChatSpaceMessageRequest} containing the details
   *                for the card header
   * @return a {@link Card.CardHeader.Builder} instance for configuring a card header
   */
  public static Card.CardHeader.Builder ofCardHeader(final GoogleChatSpaceMessageRequest request) {
    return Card.CardHeader.newBuilder()
      .setTitle(request.getTitle())
      .setSubtitle(request.getDescriptionOrSubtitle())
      .setImageUrl(request.getImageUrl())
      .setImageType(Widget.ImageType.CIRCLE);
  }

  /**
   * Creates a new {@link DecoratedText.Builder} instance for constructing decorated text.
   *
   * <p>This method initializes a {@link DecoratedText.Builder} with properties derived
   * from the provided {@link GoogleChatSpaceMessageRequest.DecoratedText}. The resulting
   * builder allows for the configuration of the decorated text, including an optional
   * start icon and the main text content before building the final
   * {@link DecoratedText} object.</p>
   *
   * @param decoratedText the {@link GoogleChatSpaceMessageRequest.DecoratedText}
   *                      containing the icon and text details
   * @return a {@link DecoratedText.Builder} instance for configuring decorated text
   */
  public static DecoratedText.Builder ofDecoratedText(final GoogleChatSpaceMessageRequest.DecoratedText decoratedText) {
    return DecoratedText.newBuilder()
      .setStartIcon(ofKnownIcon(decoratedText.getIcon()))
      .setText(decoratedText.getText());
  }

  /**
   * Creates a new {@link Divider.Builder} instance for constructing a divider.
   *
   * <p>This method initializes a {@link Divider.Builder} that can be used to create a
   * {@link Divider} element in a card. The divider is often used to separate different
   * sections of content, improving visual organization.</p>
   *
   * @return a {@link Divider.Builder} instance for configuring a divider
   */
  public static Divider.Builder ofDivider() {
    return Divider.newBuilder();
  }

  /**
   * Creates a new {@link Icon.Builder} instance for a known icon.
   *
   * <p>This method initializes a {@link Icon.Builder} that is used to set a known icon
   * based on the provided icon name. Known icons are predefined icons recognized by the
   * Google Chat API, allowing for consistent visual representation in messages.</p>
   *
   * @param iconName the name of the known icon to set
   * @return a {@link Icon.Builder} instance configured with the specified known icon
   */
  public static Icon.Builder ofKnownIcon(final String iconName) {
    return Icon.newBuilder()
      .setKnownIcon(iconName);
  }

  /**
   * Creates a new {@link Button.Builder} instance for a button in the card footer.
   *
   * <p>This method initializes a {@link Button.Builder} used to configure the primary button
   * in the card's footer. It extracts the button text, color, and click action from the
   * provided request, ensuring the button is properly set up for user interaction.</p>
   *
   * @param request the request containing the information needed to configure the button
   * @return a {@link Button.Builder} instance configured with the button's text, color, and click action
   */
  public static Button.Builder ofButton(final GoogleChatSpaceMessageRequest request) {
    return Button.newBuilder()
      .setText(request.getAccessoryButton().getText())
      .setColor(
        ofColor(
          request.getAccessoryButton().getColor().getRed(),
          request.getAccessoryButton().getColor().getGreen(),
          request.getAccessoryButton().getColor().getBlue(),
          request.getAccessoryButton().getColor().getAlpha()
        ))
      .setOnClick(ofOnClickAndOpenLink(request.getAccessoryButton().getUrl()));
  }

  /**
   * Creates a new {@link OnClick.Builder} instance that configures an action to open a link.
   *
   * <p>This method initializes an {@link OnClick.Builder} for handling click events on
   * a button or widget. It sets up the action to open the specified URL when the button
   * is clicked, allowing for easy navigation to external links.</p>
   *
   * @param url the URL to be opened when the associated widget is clicked
   * @return an {@link OnClick.Builder} instance configured to open the specified link
   */
  public static OnClick.Builder ofOnClickAndOpenLink(final String url) {
    return OnClick.newBuilder()
      .setOpenLink(ofOpenLink(url));
  }

  /**
   * Creates a new {@link OpenLink.Builder} instance configured with a specified URL.
   *
   * <p>This method initializes an {@link OpenLink.Builder} that sets the URL to be
   * opened when the associated widget is clicked. This allows for creating interactive
   * components that direct users to a specific web page or resource.</p>
   *
   * @param url the URL to be opened when the associated widget is clicked
   * @return an {@link OpenLink.Builder} instance configured with the specified URL
   */
  public static OpenLink.Builder ofOpenLink(final String url) {
    return OpenLink.newBuilder()
      .setUrl(url);
  }

  /**
   * Creates a new {@link Color.Builder} instance configured with the specified RGBA values.
   *
   * <p>This method initializes a {@link Color.Builder} to set the color using red, green,
   * blue, and alpha (transparency) values. Each color component is represented as a float
   * between 0.0 and 1.0, where 0.0 means no intensity and 1.0 means full intensity.</p>
   *
   * @param red   the red component of the color (0.0 to 1.0)
   * @param green the green component of the color (0.0 to 1.0)
   * @param blue  the blue component of the color (0.0 to 1.0)
   * @param alpha the alpha (transparency) component of the color (0.0 for fully transparent, 1.0 for fully opaque)
   * @return a {@link Color.Builder} instance configured with the specified RGBA values
   */
  public static Color.Builder ofColor(final float red, final float green, final float blue, final float alpha) {
    return Color.newBuilder()
      .setRed(red)
      .setGreen(green)
      .setBlue(blue)
      .setAlpha(FloatValue.of(alpha));
  }

  /**
   * Creates a new builder for an AccessoryWidget.
   *
   * <p>This method returns a new instance of the {@code AccessoryWidget.Builder},
   * which can be used to build an accessory widget component for a Google Chat message.
   * Accessory widgets typically provide additional functionality or interactive elements
   * within the message structure.</p>
   *
   * @return a new {@code AccessoryWidget.Builder} instance for constructing accessory widgets.
   */
  public static AccessoryWidget.Builder ofAccessoryWidget() {
    return AccessoryWidget.newBuilder();
  }

  /**
   * Creates a new builder for a ButtonList.
   *
   * <p>This method returns a new instance of the {@code ButtonList.Builder},
   * which can be used to build a list of buttons for a Google Chat message.
   * Button lists typically contain multiple interactive buttons that allow users
   * to take various actions directly from the chat interface.</p>
   *
   * @return a new {@code ButtonList.Builder} instance for constructing a list of buttons.
   */
  public static ButtonList.Builder ofButtonList() {
    return ButtonList.newBuilder();
  }
}
