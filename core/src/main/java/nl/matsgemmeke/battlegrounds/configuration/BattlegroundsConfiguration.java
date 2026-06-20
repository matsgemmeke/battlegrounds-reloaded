package nl.matsgemmeke.battlegrounds.configuration;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class BattlegroundsConfiguration extends BasePluginConfiguration {

    private static final boolean READ_ONLY = true;

    public BattlegroundsConfiguration(File file, InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public long getCameraMovementRecoilDurationMillis() {
        return this.getOptionalLong("item-camera-movement-recoil-duration-millis").orElse(20L);
    }

    public double getGunDamageAmplifier() {
        return this.checkConfigurationValue("item-gun-damage-amplifier", 1.0);
    }

    public String getLanguage() {
        return this.checkConfigurationValue("language", "en");
    }

    public long getSaveDamageEventsJobPeriodMillis() {
        return this.getOptionalLong("job-save-damage-events-period-millis").orElse(60000L);
    }

    public boolean isEnabledRegisterPlayersAsPassive() {
        return this.checkConfigurationValue("freeplay-mode-register-players-as-passive", false);
    }

    private <T> T checkConfigurationValue(String route, @Nullable T def) {
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
