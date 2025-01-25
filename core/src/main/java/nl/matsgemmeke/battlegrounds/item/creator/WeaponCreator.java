package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Object that is able to create new weapon instances of various types.
 */
public class WeaponCreator {

    @NotNull
    private Map<ItemConfiguration, WeaponFactory> configurations;

    public WeaponCreator() {
        this.configurations = new HashMap<>();
    }

    /**
     * Adds a {@link ItemConfiguration} to the provider along with its corresponding {@link WeaponFactory}.
     *
     * @param configuration the item configuration for the weapons
     * @param factory the weapon factory
     * @return whether the weapon factory was added
     */
    public boolean addConfigurationFactory(@NotNull ItemConfiguration configuration, @NotNull WeaponFactory factory) {
        configurations.put(configuration, factory);
        return configurations.get(configuration) != null;
    }

    /**
     * Gets whether a weapon id or name exists in the configuration.
     *
     * @param weaponId the weapon id
     * @return whether the weapon exists
     */
    public boolean exists(@NotNull String weaponId) {
        return this.getItemConfiguration(weaponId) != null;
    }

    /**
     * Returns the {@link WeaponFactory} which handles the creation of a specific weapon.
     *
     * @param configuration the item configuration
     * @return the corresponding weapon factory
     */
    @Nullable
    public WeaponFactory getFactory(@NotNull ItemConfiguration configuration) {
        return configurations.get(configuration);
    }

    /**
     * Finds the {@link ItemConfiguration} that belongs to the given item id.
     *
     * @param id the item id
     * @return the corresponding item configuration or null if it does not exist
     */
    @Nullable
    public ItemConfiguration getItemConfiguration(@NotNull String id) {
        for (ItemConfiguration configuration : configurations.keySet()) {
            String itemId = configuration.getItemId();

            if (itemId != null && itemId.equals(id)) {
                return configuration;
            }
        }
        return null;
    }
}
