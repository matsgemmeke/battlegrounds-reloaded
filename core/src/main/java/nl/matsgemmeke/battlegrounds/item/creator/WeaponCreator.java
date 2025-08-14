package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
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
    private final EquipmentFactory equipmentFactory;
    @NotNull
    private final FirearmFactory gunFactory;
    @NotNull
    private final Map<String, EquipmentSpec> equipmentSpecs;
    @NotNull
    private final Map<String, GunSpec> gunSpecs;

    public WeaponCreator(@NotNull EquipmentFactory equipmentFactory, @NotNull FirearmFactory gunFactory) {
        this.equipmentFactory = equipmentFactory;
        this.gunFactory = gunFactory;
        this.equipmentSpecs = new HashMap<>();
        this.gunSpecs = new HashMap<>();
    }

    public void addEquipmentSpec(@NotNull String id, @NotNull EquipmentSpec equipmentSpec) {
        equipmentSpecs.put(id, equipmentSpec);
    }

    public void addGunSpec(@NotNull String id, @NotNull GunSpec spec) {
        gunSpecs.put(id, spec);
    }

    /**
     * Creates an {@link Equipment} item for a given player in a specific game. The newly created equipment item will
     * automatically be assigned to the player.
     *
     * @param equipmentId              the equipment id
     * @param gamePlayer               the player to which the equipment will be assigned to
     * @param gameKey                  the game key
     * @throws WeaponNotFoundException when the WeaponCreator does not contain a specification for the given equipment
     *                                 id
     * @return                         an equipment instance that is created based of the given equipment id
     */
    @NotNull
    public Equipment createEquipment(@NotNull String equipmentId, @NotNull GamePlayer gamePlayer, @NotNull GameKey gameKey) {
        if (!equipmentSpecs.containsKey(equipmentId)) {
            throw new WeaponNotFoundException("The weapon creator does not contain a specification for an equipment item by the id '%s'".formatted(equipmentId));
        }

        EquipmentSpec equipmentSpec = equipmentSpecs.get(equipmentId);

        return equipmentFactory.create(equipmentSpec, gameKey, gamePlayer);
    }

    /**
     * Creates a {@link Gun} for a given player in a specific game. The newly created gun will automatically be
     * assigned to the player.
     *
     * @param gunId                    the gun id
     * @param gamePlayer               the player to which the gun will be assigned to
     * @param gameKey                  the game key
     * @throws WeaponNotFoundException when the weapon creator does not contain a specification for the given gun id
     * @return                         a gun instance that is created based of the given gun id
     */
    @NotNull
    public Gun createGun(@NotNull String gunId, @NotNull GamePlayer gamePlayer, @NotNull GameKey gameKey) {
        if (!gunSpecs.containsKey(gunId)) {
            throw new WeaponNotFoundException("The weapon creator does not contain a specification for a gun by the id '%s'".formatted(gunId));
        }

        GunSpec gunSpec = gunSpecs.get(gunId);;

        return gunFactory.create(gunSpec, gameKey, gamePlayer);
    }

    /**
     * Attempts to create a {@link Weapon} for a given player. This newly created weapon will automatically be assigned
     * to the player
     *
     * @param gamePlayer the player to create the weapon for
     * @param gameKey the game key of the game where the player is in
     * @param weaponId the weapon id
     * @throws WeaponNotFoundException when the instance does not contain a specification for the given weapon id
     * @return a weapon instance that is created based of the specification of the given weapon id
     */
    @NotNull
    public Weapon createWeapon(@NotNull GamePlayer gamePlayer, @NotNull GameKey gameKey, @NotNull String weaponId) {
        if (equipmentSpecs.containsKey(weaponId)) {
            return this.createEquipment(weaponId, gamePlayer, gameKey);
        }

        if (gunSpecs.containsKey(weaponId)) {
            return this.createGun(weaponId, gamePlayer, gameKey);
        }

        throw new WeaponNotFoundException("The weapon creator does not contain a specification for the weapon '%s'".formatted(weaponId));
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
        return Stream.of(equipmentSpecs.keySet(), gunSpecs.keySet())
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Gets whether an equipment specification is loaded with a specific id.
     *
     * @param equipmentId the equipment id
     * @return            whether an equipment specification with the given id exists
     */
    public boolean equipmentExists(@NotNull String equipmentId) {
        return equipmentSpecs.containsKey(equipmentId);
    }

    /**
     * Gets whether a gun specification is loaded with a specific id.
     *
     * @param gunId the gun id
     * @return      whether a gun specification with the given id exists
     */
    public boolean gunExists(@NotNull String gunId) {
        return gunSpecs.containsKey(gunId);
    }
}
