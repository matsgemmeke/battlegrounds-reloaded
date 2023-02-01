package com.github.matsgemmeke.battlegrounds.locale;

import org.jetbrains.annotations.NotNull;

public enum TranslationKey {

    DESCRIPTION_CREATEGAME("commands.description-creategame"),
    DESCRIPTION_GIVEWEAPON("commands.description-giveweapon"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVEGAME("commands.description-removegame"),
    DESCRIPTION_SETMAINLOBBY("commands.description-setmainlobby"),
    FREEMODE_WEAPON_GIVEN("commands.freemode-weapon-given"),
    GAME_ALREADY_EXISTS("admin.game-already-exists"),
    GAME_CONFIRM_REMOVAL("admin.game-confirm-removal"),
    GAME_CREATED("admin.game-created"),
    GAME_CREATION_FAILED("admin.game-creation-failed"),
    GAME_NOT_EXISTS("admin.game-not-exists"),
    GAME_REMOVAL_FAILED("admin.game-removal-failed"),
    GAME_REMOVED("admin.game-removed"),
    HELP_MENU_TITLE("commands.help-menu-title"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    NOT_IN_FREEMODE("errors.not-in-freemode"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
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
