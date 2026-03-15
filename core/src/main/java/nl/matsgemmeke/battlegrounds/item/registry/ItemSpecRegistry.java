package nl.matsgemmeke.battlegrounds.item.registry;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;

import java.util.*;
import java.util.stream.Stream;

public class ItemSpecRegistry {

    private final Map<String, EquipmentSpec> equipmentSpecs;
    private final Map<String, GunSpec> gunSpecs;
    private final Map<String, MeleeWeaponSpec> meleeWeaponSpecs;

    public ItemSpecRegistry() {
        this.equipmentSpecs = new HashMap<>();
        this.gunSpecs = new HashMap<>();
        this.meleeWeaponSpecs = new HashMap<>();
    }

    public void addEquipmentSpec(String id, EquipmentSpec equipmentSpec) {
        equipmentSpecs.put(id.toUpperCase(), equipmentSpec);
    }

    public void addGunSpec(String id, GunSpec spec) {
        gunSpecs.put(id.toUpperCase(), spec);
    }

    public void addMeleeWeaponSpec(String id, MeleeWeaponSpec spec) {
        meleeWeaponSpecs.put(id.toUpperCase(), spec);
    }

    /**
     * Gets whether an item specification exists by the given item name.
     *
     * @param itemName the item name
     * @return         whether an item specification exists for the item name
     */
    public boolean exists(String itemName) {
        return this.getNameList().contains(itemName.toUpperCase());
    }

    private List<String> getNameList() {
        return Stream.of(equipmentSpecs.keySet(), gunSpecs.keySet(), meleeWeaponSpecs.keySet())
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Gets an optional containing the equipment specification identified by the given name, or empty when no equipment
     * specification exists with the given name.
     *
     * @param equipmentName the equipment name
     * @return              an optional containing the corresponding equipment specification, or empty when none exist
     */
    public Optional<EquipmentSpec> getEquipmentSpec(String equipmentName) {
        return Optional.ofNullable(equipmentSpecs.get(equipmentName.toUpperCase()));
    }

    /**
     * Gets an optional containing the gun specification identified by the given name, or empty when no gun
     * specification exists with the given name.
     *
     * @param gunName the gun name
     * @return        an optional containing the corresponding gun specification, or empty when none exist
     */
    public Optional<GunSpec> getGunSpec(String gunName) {
        return Optional.ofNullable(gunSpecs.get(gunName.toUpperCase()));
    }

    /**
     * Gets an optional containing the melee weapon specification identified by the given name, or empty when no melee
     * weapon specification exists by the given name.
     *
     * @param meleeWeaponName the melee weapon name
     * @return                an optional containing the corresponding melee weapon specification, or empty when none
     *                        exist
     */
    public Optional<MeleeWeaponSpec> getMeleeWeaponSpec(String meleeWeaponName) {
        return Optional.ofNullable(meleeWeaponSpecs.get(meleeWeaponName.toUpperCase()));
    }
}
