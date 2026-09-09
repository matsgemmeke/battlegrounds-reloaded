package nl.matsgemmeke.battlegrounds.i18n;

public enum TranslationKey {

    ALREADY_IN_ARENA_MODE("errors.already-in-arena-mode"),
    ARENA_ALREADY_EXISTS("admin.arena-already-exists"),
    ARENA_CONFIRM_REMOVAL("admin.arena-confirm-removal"),
    ARENA_CREATED("admin.arena-created"),
    ARENA_HELP_MENU_FOOTER("commands.arena-help-menu-footer"),
    ARENA_HELP_MENU_HEADER("commands.arena-help-menu-header"),
    ARENA_NOT_AVAILABLE("errors.arena-not-available"),
    ARENA_NOT_EXISTS("admin.arena-not-exists"),
    ARENA_REMOVAL_FAILED("admin.arena-removal-failed"),
    ARENA_REMOVED("admin.arena-removed"),
    BATTLEGROUNDS_HELP_MENU_FOOTER("commands.battlegrounds-help-menu-footer"),
    BATTLEGROUNDS_HELP_MENU_HEADER("commands.battlegrounds-help-menu-header"),
    COMMAND_SENDER_MUST_BE_PLAYER("errors.command-sender-must-be-player"),
    DESCRIPTION_ARENA("commands.description-arena"),
    DESCRIPTION_CREATE_ARENA("commands.description-create-arena"),
    DESCRIPTION_CREATE_MAP("commands.description-create-map"),
    DESCRIPTION_ELEMENT("commands.description-element"),
    DESCRIPTION_ELEMENT_ADD("commands.description-element-add"),
    DESCRIPTION_ELEMENT_REMOVE("commands.description-element-remove"),
    DESCRIPTION_GIVE_WEAPON("commands.description-give-weapon"),
    DESCRIPTION_JOIN("commands.description-join"),
    DESCRIPTION_LOBBY("commands.description-lobby"),
    DESCRIPTION_LOBBY_SET("commands.description-lobby-set"),
    DESCRIPTION_MAP("commands.description-map"),
    DESCRIPTION_MAP_SELECT("commands.description-map-select"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVE_ARENA("commands.description-remove-arena"),
    DESCRIPTION_REMOVE_MAP("commands.description-remove-map"),
    DESCRIPTION_SET_MAIN_LOBBY("commands.description-set-main-lobby"),
    DESCRIPTION_SHOW_HITBOXES("commands.description-show-hitboxes"),
    DESCRIPTION_TOOLS("commands.description-tools"),
    ELEMENT_HELP_MENU_FOOTER("commands.element-help-menu-footer"),
    ELEMENT_HELP_MENU_HEADER("commands.element-help-menu-header"),
    ELEMENT_NOT_EXISTS("setup.element-not-exists"),
    FREEPLAY_MODE_NOT_EXISTS("errors.freeplay-mode-not-exists"),
    GENERIC_ERROR("errors.generic-error"),
    HELP_MENU_COMMAND("commands.help-menu-command"),
    INVALID_SYNTAX("errors.invalid-syntax"),
    LOBBY_HELP_MENU_FOOTER("commands.lobby-help-menu-footer"),
    LOBBY_HELP_MENU_HEADER("commands.lobby-help-menu-header"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    MAP_ALREADY_EXISTS("admin.map-already-exists"),
    MAP_CONFIRM_REMOVAL("admin.map-confirm-removal"),
    MAP_CREATED("admin.map-created"),
    MAP_NOT_EXISTS("admin.map-not-exists"),
    MAP_HELP_MENU_FOOTER("commands.map-help-menu-footer"),
    MAP_HELP_MENU_HEADER("commands.map-help-menu-header"),
    MAP_REMOVAL_FAILED("admin.map-removal-failed"),
    MAP_REMOVED("admin.map-removed"),
    MAP_SELECTED("admin.map-selected"),
    NO_MAP_SELECTED("errors.no-map-selected"),
    NOT_IN_ARENA_MODE("errors.not-in-arena-mode"),
    NOT_IN_FREEPLAY_MODE("errors.not-in-freeplay-mode"),
    PLAYER_ONLY_COMMAND("errors.player-only-command"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
    SET_LOBBY_FAILED("setup.set-lobby-failed"),
    SET_LOBBY_SUCCESSFUL("setup.set-lobby-successful"),
    SPAWN_POINT_ADDED("setup.spawn-point-added"),
    SPECIFY_ELEMENT_TYPE("setup.specify-element-type"),
    TOOL_HITBOX_SUCCESS("admin.tool-hitbox-success"),
    TOOL_NOT_EXISTS("admin.tool-not-exists"),
    TOOLS_HELP_MENU_FOOTER("commands.tools-help-menu-footer"),
    TOOLS_HELP_MENU_HEADER("commands.tools-help-menu-header"),
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
