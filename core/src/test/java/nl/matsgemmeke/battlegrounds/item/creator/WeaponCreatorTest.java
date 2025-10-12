package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.*;
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
import static org.mockito.Mockito.*;

public class WeaponCreatorTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final String EQUIPMENT_ID = "FRAG_GRENADE";
    private static final String GUN_ID = "MP5";

    private Provider<EquipmentFactory> equipmentFactoryProvider;
    private Provider<FirearmFactory> gunFactoryProvider;

    @BeforeEach
    public void setUp() {
        equipmentFactoryProvider = mock();
        gunFactoryProvider = mock();
    }

    @Test
    public void createEquipmentThrowsWeaponNotFoundExceptionWhenNoEquipmentSpecsExistByGivenEquipmentId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);

        assertThatThrownBy(() -> weaponCreator.createEquipment("fail", gamePlayer, GAME_KEY))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for an equipment item by the id 'fail'");
    }

    @Test
    public void createEquipmentReturnsEquipmentInstanceBasedOnGivenEquipmentId() {
        Equipment equipment = mock(Equipment.class);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory equipmentFactory = mock(EquipmentFactory.class);
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        when(equipmentFactoryProvider.get()).thenReturn(equipmentFactory);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        Equipment createdEquipment = weaponCreator.createEquipment(EQUIPMENT_ID, gamePlayer, GAME_KEY);

        assertThat(createdEquipment).isEqualTo(equipment);
    }

    @Test
    public void createGunThrowsWeaponNotFoundExceptionWhenNoGunSpecsExistByGivenGunId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);

        assertThatThrownBy(() -> weaponCreator.createGun("fail", gamePlayer, GAME_KEY))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for a gun by the id 'fail'");
    }

    @Test
    public void createGunReturnsGunInstanceBasedOnGivenGunId() {
        Firearm firearm = mock(Firearm.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        FirearmFactory gunFactory = mock(FirearmFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(firearm);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        Gun gun = weaponCreator.createGun(GUN_ID, gamePlayer, GAME_KEY);

        assertThat(gun).isEqualTo(firearm);
    }

    @Test
    public void createWeaponReturnsEquipmentInstanceBasedOnGivenEquipmentId() {
        Equipment equipment = mock(Equipment.class);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory equipmentFactory = mock(EquipmentFactory.class);
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        when(equipmentFactoryProvider.get()).thenReturn(equipmentFactory);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GAME_KEY, EQUIPMENT_ID);

        assertThat(weapon).isEqualTo(equipment);
    }

    @Test
    public void createWeaponReturnsGunInstanceBasedOnGivenGunId() {
        Firearm firearm = mock(Firearm.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        FirearmFactory gunFactory = mock(FirearmFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(firearm);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GAME_KEY, GUN_ID);

        assertThat(weapon).isEqualTo(firearm);
    }

    @Test
    public void createWeaponThrowsWeaponNotFoundExceptionWhenGivenIdMatchesNoneOfTheSpecifications() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);

        assertThatThrownBy(() -> weaponCreator.createWeapon(gamePlayer, GAME_KEY, "nothing"))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for the weapon 'nothing'");
    }

    @Test
    public void existsReturnsTrueWhenSpecificationOfGivenWeaponIdExists() {
        GunSpec gunSpec = this.createGunSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        boolean exists = weaponCreator.exists(GUN_ID);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseWhenSpecificationOfGivenWeaponIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        boolean exists = weaponCreator.exists(GUN_ID);

        assertThat(exists).isFalse();
    }

    @Test
    public void equipmentExistsReturnsFalseWhenWhenEquipmentSpecByGivenIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_ID);

        assertThat(equipmentExists).isFalse();
    }

    @Test
    public void equipmentExistsReturnsTrueWhenEquipmentSpecByGivenIdExists() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        weaponCreator.addEquipmentSpec(EQUIPMENT_ID, equipmentSpec);
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_ID);

        assertThat(equipmentExists).isTrue();
    }

    @Test
    public void gunExistsReturnsFalseWhenWhenGunSpecByGivenIdDoesNotExist() {
        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
        boolean gunExists = weaponCreator.gunExists(GUN_ID);

        assertThat(gunExists).isFalse();
    }

    @Test
    public void gunExistsReturnsTrueWhenGunSpecByGivenIdExists() {
        GunSpec gunSpec = this.createGunSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
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
