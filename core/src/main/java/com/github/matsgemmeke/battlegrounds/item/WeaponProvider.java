package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import com.github.matsgemmeke.battlegrounds.item.factory.WeaponFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class WeaponProvider {

    @NotNull
    private Map<ItemConfiguration, WeaponFactory<?>> factories;

    public WeaponProvider() {
        this.factories = new HashMap<>();
    }

    /**
     * Adds a {@link WeaponFactory} to the provider.
     *
     * @param configuration the item configuration for the weapons
     * @param weaponFactory the weapon factory
     * @return whether the weapon factory was added
     */
    public boolean addWeaponFactory(@NotNull ItemConfiguration configuration, @NotNull WeaponFactory<?> weaponFactory) {
        factories.put(configuration, weaponFactory);
        return factories.get(configuration) != null;
    }

    /**
     * Gets whether a weapon id or name exists in the configuration.
     *
     * @param weaponId the weapon id
     * @return whether the weapon exists
     */
    public boolean exists(@NotNull String weaponId) {
        return this.getWeaponFactory(weaponId) != null;
    }

    /**
     * Returns the {@link WeaponFactory} which handles the creation of a specific weapon.
     *
     * @param weaponId the weapon id
     * @return the corresponding {@link WeaponFactory} for the weapon
     */
    @Nullable
    public WeaponFactory<?> getWeaponFactory(@NotNull String weaponId) {
        for (ItemConfiguration configuration : factories.keySet()) {
            if (configuration.getIdList().contains(weaponId)) {
                return factories.get(configuration);
            }
        }
        return null;
    }
}
