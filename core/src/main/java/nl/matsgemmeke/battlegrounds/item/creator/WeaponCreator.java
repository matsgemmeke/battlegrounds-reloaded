package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;

import java.util.*;
import java.util.stream.Stream;

/**
 * Object that is able to create new weapon instances of various types.
 */
public class WeaponCreator {

    private final Map<String, EquipmentSpec> equipmentSpecs;
    private final Map<String, GunSpec> gunSpecs;
    private final Provider<EquipmentFactory> equipmentFactoryProvider;
    private final Provider<FirearmFactory> gunFactoryProvider;

    public WeaponCreator(Provider<EquipmentFactory> equipmentFactoryProvider, Provider<FirearmFactory> gunFactoryProvider) {
        this.equipmentFactoryProvider = equipmentFactoryProvider;
        this.gunFactoryProvider = gunFactoryProvider;
        this.equipmentSpecs = new HashMap<>();
        this.gunSpecs = new HashMap<>();
    }

    public void addEquipmentSpec(String id, EquipmentSpec equipmentSpec) {
        equipmentSpecs.put(id.toUpperCase(), equipmentSpec);
    }

    public void addGunSpec(String id, GunSpec spec) {
        gunSpecs.put(id.toUpperCase(), spec);
    }

    /**
     * Creates an {@link Equipment} item for a given player. The newly created equipment item wil automatically be
     * assigned to this player.
     *
     * @param equipmentName            the equipment id
     * @param gamePlayer               the player to which the equipment will be assigned to
     * @throws WeaponNotFoundException when the WeaponCreator does not contain a specification for the given equipment
     *                                 name
     * @return                         an equipment instance that is created based of the given equipment name
     */
    public Equipment createEquipment(String equipmentName, GamePlayer gamePlayer) {
        String upperCaseName = equipmentName.toUpperCase();

        if (!this.equipmentExists(upperCaseName)) {
            throw new WeaponNotFoundException("The weapon creator does not contain a specification for an equipment item by the name '%s'".formatted(equipmentName));
        }

        EquipmentFactory equipmentFactory = equipmentFactoryProvider.get();
        EquipmentSpec equipmentSpec = equipmentSpecs.get(upperCaseName);

        return equipmentFactory.create(equipmentSpec, gamePlayer);
    }

    /**
     * Creates a {@link Gun} for a given player. The newly created gun will automatically be assigned to this player.
     *
     * @param gunName                  the gun name
     * @param gamePlayer               the player to which the gun will be assigned to
     * @throws WeaponNotFoundException when the weapon creator does not contain a specification for the given gun name
     * @return                         a gun instance that is created based of the given gun name
     */
    public Gun createGun(String gunName, GamePlayer gamePlayer) {
        String upperCaseName = gunName.toUpperCase();

        if (!gunSpecs.containsKey(upperCaseName)) {
            throw new WeaponNotFoundException("The weapon creator does not contain a specification for a gun by the name '%s'".formatted(gunName));
        }

        FirearmFactory gunFactory = gunFactoryProvider.get();
        GunSpec gunSpec = gunSpecs.get(upperCaseName);

        return gunFactory.create(gunSpec, gamePlayer);
    }

    /**
     * Attempts to create a {@link Weapon} for a given player. This newly created weapon will automatically be assigned
     * to the player
     *
     * @param gamePlayer the player to create the weapon for
     * @param weaponName the weapon name
     * @throws WeaponNotFoundException when the instance does not contain a specification for the given weapon name
     * @return a weapon instance that is created based of the specification of the given weapon name
     */
    public Weapon createWeapon(GamePlayer gamePlayer, String weaponName) {
        if (this.equipmentExists(weaponName)) {
            return this.createEquipment(weaponName, gamePlayer);
        }

        if (this.gunExists(weaponName)) {
            return this.createGun(weaponName, gamePlayer);
        }

        throw new WeaponNotFoundException("The weapon creator does not contain a specification for the weapon '%s'".formatted(weaponName));
    }

    /**
     * Gets whether a weapon name exists in the configuration.
     *
     * @param weaponName the weapon name
     * @return           whether the weapon exists
     */
    public boolean exists(String weaponName) {
        return this.getNameList().contains(weaponName.toUpperCase());
    }

    private List<String> getNameList() {
        return Stream.of(equipmentSpecs.keySet(), gunSpecs.keySet())
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Gets whether an equipment specification is loaded with a specific name.
     *
     * @param equipmentName the equipment name
     * @return              whether an equipment specification with the given name exists
     */
    public boolean equipmentExists(String equipmentName) {
        return equipmentSpecs.containsKey(equipmentName.toUpperCase());
    }

    /**
     * Gets whether a gun specification is loaded with a specific name.
     *
     * @param gunName the gun name
     * @return        whether a gun specification with the given name exists
     */
    public boolean gunExists(String gunName) {
        return gunSpecs.containsKey(gunName.toUpperCase());
    }
}
