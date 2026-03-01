package nl.matsgemmeke.battlegrounds.item.creator;

import com.google.inject.Provider;
import nl.matsgemmeke.battlegrounds.configuration.item.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.melee.MeleeWeaponSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.SpecDeserializer;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.item.Weapon;
import nl.matsgemmeke.battlegrounds.item.equipment.Equipment;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunFactory;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponFactory;
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
    private static final String MELEE_WEAPON_NAME = "Combat Knife";

    @Mock
    private Provider<EquipmentFactory> equipmentFactoryProvider;
    @Mock
    private Provider<GunFactory> gunFactoryProvider;
    @Mock
    private Provider<MeleeWeaponFactory> meleeWeaponFactoryProvider;

    private WeaponCreator weaponCreator;

    @BeforeEach
    void setUp() {
        weaponCreator = new WeaponCreator(equipmentFactoryProvider, gunFactoryProvider, meleeWeaponFactoryProvider);
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
        Gun gun = mock(Gun.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        GunFactory gunFactory = mock(GunFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(gun);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        Gun result = weaponCreator.createGun(GUN_NAME, gamePlayer);

        assertThat(result).isEqualTo(gun);
    }

    @Test
    void createMeleeWeaponThrowsWeaponNotFoundExceptionWhenNoMeleeWeaponSpecsExistByGivenName() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        assertThatThrownBy(() -> weaponCreator.createMeleeWeapon("fail", gamePlayer))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for a melee weapon by the name 'fail'");
    }

    @Test
    void createMeleeWeaponReturnsMeleeWeaponInstanceBasedOnGivenName() {
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        MeleeWeaponSpec meleeWeaponSpec = this.createMeleeWeaponSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeaponFactory meleeWeaponFactory = mock(MeleeWeaponFactory.class);
        when(meleeWeaponFactory.create(meleeWeaponSpec)).thenReturn(meleeWeapon);

        when(meleeWeaponFactoryProvider.get()).thenReturn(meleeWeaponFactory);

        weaponCreator.addMeleeWeaponSpec(MELEE_WEAPON_NAME, meleeWeaponSpec);
        MeleeWeapon result = weaponCreator.createMeleeWeapon(MELEE_WEAPON_NAME, gamePlayer);

        assertThat(result).isEqualTo(meleeWeapon);

        verify(meleeWeapon).assign(gamePlayer);
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
        Gun gun = mock(Gun.class);
        GunSpec gunSpec = this.createGunSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        GunFactory gunFactory = mock(GunFactory.class);
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(gun);

        when(gunFactoryProvider.get()).thenReturn(gunFactory);

        weaponCreator.addGunSpec(GUN_NAME, gunSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, GUN_NAME);

        assertThat(weapon).isEqualTo(gun);
    }

    @Test
    void createWeaponReturnsMeleeWeaponInstanceBasedOnGivenMeleeWeaponName() {
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);
        MeleeWeaponSpec meleeWeaponSpec = this.createMeleeWeaponSpec();
        GamePlayer gamePlayer = mock(GamePlayer.class);

        MeleeWeaponFactory meleeWeaponFactory = mock(MeleeWeaponFactory.class);
        when(meleeWeaponFactory.create(meleeWeaponSpec)).thenReturn(meleeWeapon);

        when(meleeWeaponFactoryProvider.get()).thenReturn(meleeWeaponFactory);

        weaponCreator.addMeleeWeaponSpec(MELEE_WEAPON_NAME, meleeWeaponSpec);
        Weapon weapon = weaponCreator.createWeapon(gamePlayer, MELEE_WEAPON_NAME);

        assertThat(weapon).isEqualTo(meleeWeapon);

        verify(meleeWeapon).assign(gamePlayer);
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

    @Test
    void meleeWeaponExistsReturnsFalseWhenWhenMeleeWeaponSpecByGivenNameDoesNotExist() {
        boolean meleeWeaponExists = weaponCreator.meleeWeaponExists(MELEE_WEAPON_NAME);

        assertThat(meleeWeaponExists).isFalse();
    }

    @Test
    void meleeWeaponExistsReturnsTrueWhenMeleeWeaponSpecByGivenNameExists() {
        MeleeWeaponSpec meleeWeaponSpec = this.createMeleeWeaponSpec();

        weaponCreator.addMeleeWeaponSpec(MELEE_WEAPON_NAME, meleeWeaponSpec);
        boolean meleeWeaponExists = weaponCreator.meleeWeaponExists(MELEE_WEAPON_NAME);

        assertThat(meleeWeaponExists).isTrue();
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

    private MeleeWeaponSpec createMeleeWeaponSpec() {
        File file = new File("src/main/resources/items/melee_weapons/combat_knife.yml");

        SpecDeserializer specDeserializer = new SpecDeserializer();
        return specDeserializer.deserializeSpec(file, MeleeWeaponSpec.class);
    }
}
