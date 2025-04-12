package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Object that is able to create new weapon instances of various types.
 */
public class WeaponCreator {

    @NotNull
    private FirearmFactory firearmFactory;
    @NotNull
    private Map<String, ItemConfiguration> configurations;
    @NotNull
    private Map<String, GunSpecification> gunSpecifications;

    public WeaponCreator(@NotNull FirearmFactory firearmFactory) {
        this.firearmFactory = firearmFactory;
        this.configurations = new HashMap<>();
        this.gunSpecifications = new HashMap<>();
    }

    /**
     * Adds a {@link ItemConfiguration} to the provider along with its corresponding {@link WeaponFactory}.
     *
     * @param configuration the item configuration for the weapons
     * @return whether the weapon factory was added
     */
    public void addConfigurationFactory(@NotNull String weaponId, @NotNull ItemConfiguration configuration) {
        configurations.put(weaponId, configuration);
    }

    public void addGunSpecification(@NotNull String id, @NotNull GunSpecification specification) {
        gunSpecifications.put(id, specification);
    }

    /**
     * Attempts to create a {@link Weapon} for a given player. This newly created weapon will automatically be assigned
     * to the player
     *
     * @param gamePlayer the player to create the weapon for
     * @param gameKey the game key of the game where the player is in
     * @param weaponId the weapon id
     * @throws IllegalArgumentException when the instance does not contain a specification for the given weapon id
     * @return a weapon instance that is created based of the specification of the given weapon id
     */
    @NotNull
    public Weapon createWeapon(@NotNull GamePlayer gamePlayer, @NotNull GameKey gameKey, @NotNull String weaponId) {
        if (gunSpecifications.containsKey(weaponId)) {
            GunSpecification specification = gunSpecifications.get(weaponId);
            ItemConfiguration configuration = configurations.get(weaponId);

            return firearmFactory.create(specification, configuration, gameKey, gamePlayer);
        }

        throw new IllegalArgumentException("The weapon creator does not contain a specification for the weapon '%s'".formatted(weaponId));
    }

    /**
     * Gets whether a weapon id or name exists in the configuration.
     *
     * @param weaponId the weapon id
     * @return whether the weapon exists
     */
    public boolean exists(@NotNull String weaponId) {
        return this.getIdList().contains(weaponId);
    }

    private List<String> getIdList() {
        return Stream.of(gunSpecifications.keySet())
                .flatMap(Collection::stream)
                .toList();
    }
}
