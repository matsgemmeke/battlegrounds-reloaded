package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeaponCreatorTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final String EQUIPMENT_ID = "FRAG_GRENADE";
    private static final String GUN_ID = "MP5";

    private EquipmentFactory equipmentFactory;
    private FirearmFactory firearmFactory;

    @BeforeEach
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
    }

    @Test
    public void createEquipmentThrowsWeaponNotFoundExceptionWhenNoEquipmentSpecsExistByGivenEquipmentId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);

        assertThatThrownBy(() -> weaponCreator.createEquipment("fail", gamePlayer, GAME_KEY))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for an equipment item by the id 'fail'");
    }

    @Test
    public void createEquipmentReturnsEquipmentInstanceBasedOnGivenEquipmentId() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Equipment equipment = mock(Equipment.class);
        when(equipmentFactory.create(equipmentSpec, GAME_KEY, gamePlayer)).thenReturn(equipment);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        Equipment createdEquipment = weaponCreator.createEquipment(EQUIPMENT_ID, gamePlayer, GAME_KEY);

        assertThat(createdEquipment).isEqualTo(equipment);
    }

    @Test
    public void createGunThrowsWeaponNotFoundExceptionWhenNoGunSpecsExistByGivenGunId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);

        assertThatThrownBy(() -> weaponCreator.createGun("fail", gamePlayer, GAME_KEY))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for a gun by the id 'fail'");
    }

    @Test
    public void createGunReturnsGunInstanceBasedOnGivenGunId() {
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        Firearm firearm = mock(Firearm.class);
        when(firearmFactory.create(gunSpec, GAME_KEY, gamePlayer)).thenReturn(firearm);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        Gun gun = weaponCreator.createGun(GUN_ID, gamePlayer, GAME_KEY);

        assertThat(gun).isEqualTo(firearm);
    }

    @Test
    public void createWeaponReturnsEquipmentInstanceBasedOnGivenEquipmentId() {
        Equipment equipment = mock(Equipment.class);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(equipmentFactory.create(equipmentSpec, GAME_KEY, gamePlayer)).thenReturn(equipment);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GAME_KEY, EQUIPMENT_ID);

        assertThat(weapon).isEqualTo(equipment);
    }

    @Test
    public void createWeaponReturnsGunInstanceBasedOnGivenGunId() {
        Firearm firearm = mock(Firearm.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(firearmFactory.create(gunSpec, GAME_KEY, gamePlayer)).thenReturn(firearm);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GAME_KEY, GUN_ID);

        assertThat(weapon).isEqualTo(firearm);
    }

    @Test
    public void createWeaponThrowsWeaponNotFoundExceptionWhenGivenIdMatchesNoneOfTheSpecifications() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);

        assertThatThrownBy(() -> weaponCreator.createWeapon(gamePlayer, GAME_KEY, "nothing"))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for the weapon 'nothing'");
    }

    @Test
    public void existsReturnsTrueWhenSpecificationOfGivenWeaponIdExists() {
        GunSpec gunSpec = this.createGunSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        boolean exists = weaponCreator.exists(GUN_ID);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseWhenSpecificationOfGivenWeaponIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        boolean exists = weaponCreator.exists(GUN_ID);

        assertThat(exists).isFalse();
    }

    @Test
    public void equipmentExistsReturnsFalseWhenWhenEquipmentSpecByGivenIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_ID);

        assertThat(equipmentExists).isFalse();
    }

    @Test
    public void equipmentExistsReturnsTrueWhenEquipmentSpecByGivenIdExists() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_ID);

        assertThat(equipmentExists).isTrue();
    }

    @Test
    public void gunExistsReturnsFalseWhenWhenGunSpecByGivenIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        boolean gunExists = weaponCreator.gunExists(GUN_ID);

        assertThat(gunExists).isFalse();
    }

    @Test
    public void gunExistsReturnsTrueWhenGunSpecByGivenIdExists() {
        GunSpec gunSpec = this.createGunSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        boolean gunExists = weaponCreator.gunExists(GUN_ID);

        assertThat(gunExists).isTrue();
    }

    private EquipmentSpec createEquipmentSpec() {
        File file = new File("src/main/resources/items/lethal_equipment/frag_grenade.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, EquipmentSpec.class);
    }

    private GunSpec createGunSpec() {
        File file = new File("src/main/resources/items/submachine_guns/mp5.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, GunSpec.class);
    }
}
