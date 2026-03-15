package nl.matsgemmeke.battlegrounds.game.component.item;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunFactory;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponFactory;
import nl.matsgemmeke.battlegrounds.item.registry.ItemSpecRegistry;

public class ItemCreator {

    private final ItemSpecRegistry itemSpecRegistry;
    private final EquipmentFactory equipmentFactory;
    private final GunFactory gunFactory;
    private final MeleeWeaponFactory meleeWeaponFactory;

    @Inject
    public ItemCreator(ItemSpecRegistry itemSpecRegistry, EquipmentFactory equipmentFactory, GunFactory gunFactory, MeleeWeaponFactory meleeWeaponFactory) {
        this.itemSpecRegistry = itemSpecRegistry;
        this.equipmentFactory = equipmentFactory;
        this.gunFactory = gunFactory;
        this.meleeWeaponFactory = meleeWeaponFactory;
    }

    /**
     * Creates an {@link Equipment} item for a given player. The newly created equipment item wil automatically be
     * assigned to this player.
     *
     * @param equipmentName          the equipment id
     * @param gamePlayer             the player to which the equipment will be assigned to
     * @throws ItemNotFoundException when the item spec registry does not contain an equipment specification for the
     *                               given name
     * @return                       an equipment instance that is created based on the given name
     */
    public Equipment createEquipment(String equipmentName, GamePlayer gamePlayer) {
        EquipmentSpec equipmentSpec = itemSpecRegistry.getEquipmentSpec(equipmentName).orElse(null);

        if (equipmentSpec == null) {
            throw new ItemNotFoundException("The item spec registry does not contain an equipment specification by the name '%s'".formatted(equipmentName));
        }

        return equipmentFactory.create(equipmentSpec, gamePlayer);
    }

    /**
     * Creates a {@link Gun} for a given player. The newly created gun will automatically be assigned to this player.
     *
     * @param gunName                the gun name
     * @param gamePlayer             the player to which the gun will be assigned to
     * @throws ItemNotFoundException when the item spec registry does not contain a gun specification for the given name
     * @return                       a gun instance that is created based on the given name
     */
    public Gun createGun(String gunName, GamePlayer gamePlayer) {
        GunSpec gunSpec = itemSpecRegistry.getGunSpec(gunName).orElse(null);

        if (gunSpec == null) {
            throw new ItemNotFoundException("The item spec registry does not contain a gun specification by the name '%s'".formatted(gunName));
        }

        return gunFactory.create(gunSpec, gamePlayer);
    }

    /**
     * Creates a {@link MeleeWeapon} for a given player. The newly created melee weapon will automatically be assigned
     * to this player.
     *
     * @param meleeWeaponName        the melee weapon name
     * @param gamePlayer             the player to which the melee weapon will be assigned to
     * @throws ItemNotFoundException when the item spec registry does not contain a melee weapon specification for the
     *                               given name
     * @return                       a melee weapon instance that is created based on the given name
     */
    public MeleeWeapon createMeleeWeapon(String meleeWeaponName, GamePlayer gamePlayer) {
        MeleeWeaponSpec meleeWeaponSpec = itemSpecRegistry.getMeleeWeaponSpec(meleeWeaponName).orElse(null);

        if (meleeWeaponSpec == null) {
            throw new ItemNotFoundException("The item spec registry does not contain a melee weapon specification by the name '%s'".formatted(meleeWeaponName));
        }

        MeleeWeapon meleeWeapon = meleeWeaponFactory.create(meleeWeaponSpec);
        meleeWeapon.assign(gamePlayer);
        return meleeWeapon;
    }

    /**
     * Attempts to create a {@link Weapon} for a given player. This newly created weapon will automatically be assigned
     * to the player
     *
     * @param gamePlayer             the player to create the weapon for
     * @param weaponName             the weapon name
     * @throws ItemNotFoundException when the item spec registry does not contain any specification for the given
     *                               weapon name
     * @return                       a weapon instance that is created based of the specification of the given weapon name
     */
    public Weapon createWeapon(GamePlayer gamePlayer, String weaponName) {
        if (itemSpecRegistry.getEquipmentSpec(weaponName).isPresent()) {
            return this.createEquipment(weaponName, gamePlayer);
        }

        if (itemSpecRegistry.getGunSpec(weaponName).isPresent()) {
            return this.createGun(weaponName, gamePlayer);
        }

        if (itemSpecRegistry.getMeleeWeaponSpec(weaponName).isPresent()) {
            return this.createMeleeWeapon(weaponName, gamePlayer);
        }

        throw new ItemNotFoundException("The item spec registry does not contain a specification for the weapon '%s'".formatted(weaponName));
    }
}
