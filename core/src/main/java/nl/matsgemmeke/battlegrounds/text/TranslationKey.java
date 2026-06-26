package nl.matsgemmeke.battlegrounds.text;

public enum TranslationKey {

    ARENA_ALREADY_EXISTS("admin.arena-already-exists"),
    ARENA_CONFIRM_REMOVAL("admin.arena-confirm-removal"),
    ARENA_CREATED("admin.arena-created"),
    ARENA_CREATION_FAILED("admin.arena-creation-failed"),
    ARENA_HELP_MENU_TITLE("commands.arena-help-menu-title"),
    ARENA_NOT_EXISTS("admin.arena-not-exists"),
    ARENA_REMOVAL_FAILED("admin.arena-removal-failed"),
    ARENA_REMOVED("admin.arena-removed"),
    BATTLEGROUNDS_HELP_MENU_TITLE("commands.battlegrounds-help-menu-title"),
    COMMAND_SENDER_MUST_BE_PLAYER("errors.command-sender-must-be-player"),
    DESCRIPTION_ARENA("commands.description-arena"),
    DESCRIPTION_CREATE_ARENA("commands.description-create-arena"),
    DESCRIPTION_GIVE_WEAPON("commands.description-give-weapon"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVE_ARENA("commands.description-remove-arena"),
    DESCRIPTION_SET_MAIN_LOBBY("commands.description-set-main-lobby"),
    DESCRIPTION_SHOW_HITBOXES("commands.description-show-hitboxes"),
    DESCRIPTION_TOOLS("commands.description-tools"),
    FREEPLAY_MODE_NOT_EXISTS("errors.freeplay-mode-not-exists"),
    HELP_MENU_COMMAND("commands.help-menu-command"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    NOT_IN_FREEPLAY_MODE("errors.not-in-freeplay-mode"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
    TOOL_HITBOX_SUCCESS("admin.tool-hitbox-success"),
    TOOL_NOT_EXISTS("admin.tool-not-exists"),
    TOOLS_HELP_MENU_TITLE("commands.tools-help-menu-title"),
    UNKNOWN_COMMAND("errors.unknown-command"),
    WEAPON_GIVEN("commands.weapon-given"),
    WEAPON_NOT_EXISTS("admin.weapon-not-exists");

    private final String path;

    TranslationKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
