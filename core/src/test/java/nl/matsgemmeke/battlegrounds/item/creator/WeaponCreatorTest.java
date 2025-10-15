package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeaponCreatorTest {

    private static final String EQUIPMENT_NAME = "Frag Grenade";
    private static final String GUN_NAME = "MP5";

    @Mock
    private Provider<EquipmentFactory> equipmentFactoryProvider;
    @Mock
    private Provider<FirearmFactory> gunFactoryProvider;

    private WeaponCreator weaponCreator;

    @BeforeEach
    void setUp() {
        weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider);
    }

    @Test
    void createEquipmentThrowsWeaponNotFoundExceptionWhenNoEquipmentSpecsExistByGivenEquipmentName() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        assertThatThrownBy(() -> weaponCreator.createEquipment("fail", gamePlayer))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for an equipment item by the name 'fail'");
    }

    @Test
    void createEquipmentReturnsEquipmentInstanceBasedOnGivenEquipmentName() {
        Equipment equipment = mock(Equipment.class);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory equipmentFactory = mock(EquipmentFactory.class);
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        when(equipmentFactoryProvider.get()).thenReturn(equipmentFactory);

        weaponCreator.addEquipmentSpec(EQUIPMENT_NAME, equipmentSpec);
        Equipment createdEquipment = weaponCreator.createEquipment(EQUIPMENT_NAME, gamePlayer);

        assertThat(createdEquipment).isEqualTo(equipment);
    }

    @Test
    void createGunThrowsWeaponNotFoundExceptionWhenNoGunSpecsExistByGivenGunName() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        assertThatThrownBy(() -> weaponCreator.createGun("fail", gamePlayer))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for a gun by the name 'fail'");
    }

    @Test
    void createGunReturnsGunInstanceBasedOnGivenGunName() {
        Firearm firearm = mock(Firearm.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        FirearmFactory gunFactory = mock(FirearmFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(firearm);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        Gun gun = weaponCreator.createGun(GUN_NAME, gamePlayer);

        assertThat(gun).isEqualTo(firearm);
    }

    @Test
    void createWeaponReturnsEquipmentInstanceBasedOnGivenEquipmentName() {
        Equipment equipment = mock(Equipment.class);
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        EquipmentFactory equipmentFactory = mock(EquipmentFactory.class);
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        when(equipmentFactoryProvider.get()).thenReturn(equipmentFactory);

        weaponCreator.addEquipmentSpec(EQUIPMENT_NAME, equipmentSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, EQUIPMENT_NAME);

        assertThat(weapon).isEqualTo(equipment);
    }

    @Test
    void createWeaponReturnsGunInstanceBasedOnGivenGunName() {
        Firearm firearm = mock(Firearm.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        FirearmFactory gunFactory = mock(FirearmFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(firearm);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GUN_NAME);

        assertThat(weapon).isEqualTo(firearm);
    }

    @Test
    void createWeaponThrowsWeaponNotFoundExceptionWhenGivenNameMatchesNoneOfTheSpecifications() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        assertThatThrownBy(() -> weaponCreator.createWeapon(gamePlayer, "nothing"))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for the weapon 'nothing'");
    }

    @Test
    void existsReturnsTrueWhenSpecificationOfGivenWeaponNameExists() {
        GunSpec gunSpec = this.createGunSpec();

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        boolean exists = weaponCreator.exists(GUN_NAME);

        assertThat(exists).isTrue();
    }

    @Test
    void existsReturnsFalseWhenSpecificationOfGivenWeaponNameDoesNotExist() {
        boolean exists = weaponCreator.exists(GUN_NAME);

        assertThat(exists).isFalse();
    }

    @Test
    void equipmentExistsReturnsFalseWhenWhenEquipmentSpecByGivenNameDoesNotExist() {
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_NAME);

        assertThat(equipmentExists).isFalse();
    }

    @Test
    void equipmentExistsReturnsTrueWhenEquipmentSpecByGivenNameExists() {
        EquipmentSpec equipmentSpec = this.createEquipmentSpec();

        weaponCreator.addEquipmentSpec(EQUIPMENT_NAME, equipmentSpec);
        boolean equipmentExists = weaponCreator.equipmentExists(EQUIPMENT_NAME);

        assertThat(equipmentExists).isTrue();
    }

    @Test
    void gunExistsReturnsFalseWhenWhenGunSpecByGivenNameDoesNotExist() {
        boolean gunExists = weaponCreator.gunExists(GUN_NAME);

        assertThat(gunExists).isFalse();
    }

    @Test
    void gunExistsReturnsTrueWhenGunSpecByGivenNameExists() {
        GunSpec gunSpec = this.createGunSpec();

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        boolean gunExists = weaponCreator.gunExists(GUN_NAME);

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
