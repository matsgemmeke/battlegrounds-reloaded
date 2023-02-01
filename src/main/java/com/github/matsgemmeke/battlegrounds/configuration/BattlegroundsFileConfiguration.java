package com.github.matsgemmeke.battlegrounds.configuration;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.InputStream;

public class BattlegroundsFileConfiguration extends AbstractPluginConfiguration implements BattlegroundsConfig {

    private static final boolean READ_ONLY = true;

    public BattlegroundsFileConfiguration(@NotNull File file, @Nullable InputStream resource) {
        super(file, resource, READ_ONLY);
    }

    public double getFirearmDamageAmplifier() {
        return this.checkConfigurationValue("item-firearm-damage-amplifier", 1.0);
    }

    public double getFirearmRecoilAmplifier() {
        return this.checkConfigurationValue("item-firearm-recoil-amplifier", 10.0);
    }

    @NotNull
    public String getFirearmTriggerSound() {
        return this.checkConfigurationValue("item-firearm-trigger-sound");
    }

    @NotNull
    public String getLanguage() {
        return this.checkConfigurationValue("language", "en");
    }

    private <T> T checkConfigurationValue(@NotNull String route) {
        return this.checkConfigurationValue(route, null);
    }

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
