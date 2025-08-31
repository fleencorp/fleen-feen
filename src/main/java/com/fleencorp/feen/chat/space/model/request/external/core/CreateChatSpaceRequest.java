package com.fleencorp.feen.chat.space.model.request.external.core;

import com.fleencorp.feen.common.constant.external.google.chat.space.ChatHistoryState;
import com.fleencorp.feen.common.constant.external.google.chat.space.ChatSpaceThreadState;
import com.fleencorp.feen.common.constant.external.google.chat.space.ChatSpaceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

import static com.fleencorp.feen.common.constant.external.google.chat.space.permission.ChatSpacePermissionField.*;
import static com.fleencorp.feen.common.constant.external.google.chat.space.permission.PermissionSettingField.managersAllowed;
import static com.fleencorp.feen.common.constant.external.google.chat.space.permission.PermissionSettingField.membersAllowed;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatSpaceRequest extends ChatSpaceRequest {

  protected String displayName;
  protected String description;
  protected String guidelinesOrRules;
  protected ChatHistoryState historyState;
  protected ChatSpaceType spaceType;
  protected ChatSpaceThreadState threadState;
  protected boolean externalUsersAllowed;
  protected boolean importMode;
  protected Long chatSpaceId;
  protected String userEmailAddress;

  public boolean isExternalUsersAllowed() {
    return true;
  }

  public String getHistoryState() {
    return ChatHistoryState.HISTORY_ON.getValue();
  }

  public String getSpaceType() {
    return ChatSpaceType.SPACE.getValue();
  }

  public String getThreadState() {
    return ChatSpaceThreadState.THREADED_MESSAGES.getValue();
  }

  public Map<String, Object> getPermissions() {
    final Map<String, Object> permissions = new HashMap<>();
    permissions.put(manageMembersAndGroups(), PermissionSettings.defaultPermissions());
    permissions.put(modifySpaceDetails(), PermissionSettings.defaultPermissions());
    permissions.put(toggleHistory(), PermissionSettings.defaultPermissions());
    permissions.put(useAtMentionAll(), PermissionSettings.defaultPermissions());
    permissions.put(manageApps(), PermissionSettings.defaultPermissions());
    permissions.put(manageWebhooks(), PermissionSettings.defaultPermissions());
    permissions.put(replyMessages(), PermissionSettings.permitMembers());

    return permissions;
  }

  public static CreateChatSpaceRequest of(final String displayName, final String description, final String guidelinesOrRules, final String userEmailAddress) {
    final CreateChatSpaceRequest request = new CreateChatSpaceRequest();
    request.setDisplayName(displayName);
    request.setDescription(description);
    request.setGuidelinesOrRules(guidelinesOrRules);
    request.setUserEmailAddress(userEmailAddress);
    request.setHistoryState(ChatHistoryState.HISTORY_ON);
    request.setSpaceType(ChatSpaceType.SPACE);
    request.setThreadState(ChatSpaceThreadState.THREADED_MESSAGES);
    request.setExternalUsersAllowed(true);

    return request;
  }

  @Getter
  @Setter
  public static final class PermissionSettings {

    private PermissionSettings() {}

    public static Map<String, Object> defaultPermissions() {
      final Map<String, Object> permissionSettings = new HashMap<>();
      permissionSettings.put(managersAllowed(), true);
      permissionSettings.put(membersAllowed(), false);

      return permissionSettings;
    }

    public static Map<String, Object> permitMembers() {
      final Map<String, Object> permissionSettings = defaultPermissions();
      permissionSettings.put(membersAllowed(), true);

      return permissionSettings;
    }
  }
}
