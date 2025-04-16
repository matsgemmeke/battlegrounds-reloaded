package nl.matsgemmeke.battlegrounds.configuration;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class BattlegroundsConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;

    public BattlegroundsConfiguration(@NotNull File file, @Nullable InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public long getCameraMovementRecoilDurationInMilliseconds() {
        return this.checkConfigurationValue("item-camera-movement-recoil-duration-in-ms", 20);
    }

    public double getGunDamageAmplifier() {
        return this.checkConfigurationValue("item-gun-damage-amplifier", 1.0);
    }

    @NotNull
    public String getGunTriggerSound() {
        return this.checkConfigurationValue("item-gun-trigger-sounds", "UI_BUTTON_CLICK-0.5-2-0");
    }

    @NotNull
    public String getLanguage() {
        return this.checkConfigurationValue("language", "en");
    }

    public boolean isEnabledRegisterPlayersAsPassive() {
        return this.checkConfigurationValue("training-mode-register-players-as-passive", false);
    }

    @NotNull
    private <T> T checkConfigurationValue(@NotNull String route) {
        return this.checkConfigurationValue(route, null);
    }

    @NotNull
    private <T> T checkConfigurationValue(@NotNull String route, @Nullable T def) {
        Object obj = this.get(route);

        if (obj == null) {
            if (def != null) {
                return def;
            }
            throw new NullPointerException("Missing configuration value for route " + route);
        }

        return (T) obj;
    }
}
