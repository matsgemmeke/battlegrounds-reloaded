package nl.matsgemmeke.battlegrounds.text;

import org.jetbrains.annotations.NotNull;

public enum TranslationKey {

    COMMAND_SENDER_MUST_BE_PLAYER("errors.command-sender-must-be-player"),
    DESCRIPTION_CREATESESSION("commands.description-createsession"),
    DESCRIPTION_GIVEWEAPON("commands.description-giveweapon"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVESESSION("commands.description-removesession"),
    DESCRIPTION_SETMAINLOBBY("commands.description-setmainlobby"),
    DESCRIPTION_TOOLS("commands.description-tools"),
    HELP_MENU_COMMAND("commands.help-menu-command"),
    HELP_MENU_TITLE("commands.help-menu-title"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    NOT_IN_OPEN_MODE("errors.not-in-open-mode"),
    OPEN_MODE_NOT_EXISTS("errors.open-mode.not-exists"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
    SESSION_ALREADY_EXISTS("admin.session-already-exists"),
    SESSION_CONFIRM_REMOVAL("admin.session-confirm-removal"),
    SESSION_CREATED("admin.session-created"),
    SESSION_CREATION_FAILED("admin.session-creation-failed"),
    SESSION_NOT_EXISTS("admin.session-not-exists"),
    SESSION_REMOVAL_FAILED("admin.session-removal-failed"),
    SESSION_REMOVED("admin.session-removed"),
    TOOL_HITBOX_SUCCESSFUL("admin.tool-hitbox-successful"),
    TOOL_NOT_EXISTS("admin.tool-not-exists"),
    TOOLS_MENU_TITLE("commands.tools-menu-title"),
    UNKNOWN_COMMAND("errors.unknown-command"),
    WEAPON_GIVEN("commands.weapon-given"),
    WEAPON_NOT_EXISTS("admin.weapon-not-exists");

    @NotNull
    private String path;

    TranslationKey(@NotNull String path) {
        this.path = path;
    }

    @NotNull
    public String getPath() {
        return path;
    }
}
