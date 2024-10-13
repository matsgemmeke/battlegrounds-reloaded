package nl.matsgemmeke.battlegrounds.text;

import org.jetbrains.annotations.NotNull;

public enum TranslationKey {

    DESCRIPTION_CREATESESSION("commands.description-createsession"),
    DESCRIPTION_GIVEWEAPON("commands.description-giveweapon"),
    DESCRIPTION_RELOAD("commands.description-reload"),
    DESCRIPTION_REMOVESESSION("commands.description-removesession"),
    DESCRIPTION_SETMAINLOBBY("commands.description-setmainlobby"),
    HELP_MENU_TITLE("commands.help-menu-title"),
    MAIN_LOBBY_SET("admin.main-lobby-set"),
    NOT_IN_TRAINING_MODE("errors.not-in-training-mode"),
    RELOAD_FAILED("admin.reload-failed"),
    RELOAD_SUCCESS("admin.reload-success"),
    SESSION_ALREADY_EXISTS("admin.session-already-exists"),
    SESSION_CONFIRM_REMOVAL("admin.session-confirm-removal"),
    SESSION_CREATED("admin.session-created"),
    SESSION_CREATION_FAILED("admin.session-creation-failed"),
    SESSION_NOT_EXISTS("admin.session-not-exists"),
    SESSION_REMOVAL_FAILED("admin.session-removal-failed"),
    SESSION_REMOVED("admin.session-removed"),
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
