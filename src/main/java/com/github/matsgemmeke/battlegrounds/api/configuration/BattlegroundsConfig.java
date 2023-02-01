package com.github.matsgemmeke.battlegrounds.api.configuration;

import org.jetbrains.annotations.NotNull;

public interface BattlegroundsConfig extends PluginConfiguration {

    /**
     * Gets the firearm damage amplifier setting.
     *
     * @return the firearm damage amplifier setting
     */
    double getFirearmDamageAmplifier();

    /**
     * Gets the firearm recoil amplifier setting.
     *
     * @return the firearm recoil amplifier setting
     */
    double getFirearmRecoilAmplifier();

    /**
     * Gets the firearm trigger sound setting.
     *
     * @return the firearm trigger sound setting
     */
    @NotNull
    String getFirearmTriggerSound();

    /**
     * Gets the language setting.
     *
     * @return the language setting
     */
    @NotNull
    String getLanguage();
}
