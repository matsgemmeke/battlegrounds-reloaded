package nl.matsgemmeke.battlegrounds.text;

public enum TranslationKey {

    ARENA_ALREADY_EXISTS("admin.arena-already-exists"),
    ARENA_CONFIRM_REMOVAL("admin.arena-confirm-removal"),
    ARENA_CREATED("admin.arena-created"),
    ARENA_CREATION_FAILED("admin.arena-creation-failed"),
    ARENA_NOT_EXISTS("admin.arena-not-exists"),
    ARENA_REMOVAL_FAILED("admin.arena-removal-failed"),
    ARENA_REMOVED("admin.arena-removed"),
    COMMAND_SENDER_MUST_BE_PLAYER("errors.command-sender-must-be-player"),
    DESCRIPTION_CREATEARENA("commands.description-createarena"),
    DESCRIPTION_GIVEWEAPON("commands.description-giveweapon"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVEARENA("commands.description-removearena"),
    DESCRIPTION_SETMAINLOBBY("commands.description-setmainlobby"),
    DESCRIPTION_TOOLS("commands.description-tools"),
    DESCRIPTION_TOOLS_HITBOX("commands.description-tools-hitbox"),
    FREEPLAY_MODE_NOT_EXISTS("errors.freeplay-mode-not-exists"),
    HELP_MENU_COMMAND("commands.help-menu-command"),
    HELP_MENU_TITLE("commands.help-menu-title"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    NOT_IN_FREEPLAY_MODE("errors.not-in-freeplay-mode"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
    TOOL_HITBOX_SUCCESS("admin.tool-hitbox-success"),
    TOOL_NOT_EXISTS("admin.tool-not-exists"),
    TOOLS_MENU_COMMAND("commands.tools-menu-command"),
    TOOLS_MENU_TITLE("commands.tools-menu-title"),
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
