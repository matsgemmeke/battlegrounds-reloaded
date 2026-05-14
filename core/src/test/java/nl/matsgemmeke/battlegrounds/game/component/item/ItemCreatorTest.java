package nl.matsgemmeke.battlegrounds.game.component.item;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemCreatorTest {

    private static final String EQUIPMENT_NAME = "Frag Grenade";
    private static final String GUN_NAME = "MP5";
    private static final String MELEE_WEAPON_NAME = "Combat Knife";

    @Mock
    private ItemSpecRegistry itemSpecRegistry;
    @Mock
    private EquipmentFactory equipmentFactory;
    @Mock
    private GunFactory gunFactory;
    @Mock
    private MeleeWeaponFactory meleeWeaponFactory;
    @Mock
    private GamePlayer gamePlayer;
    @InjectMocks
    private ItemCreator itemCreator;

    @Test
    @DisplayName("createEquipment throws ItemNotFoundException when no equipment specification exists by given name")
    void createEquipment_equipmentSpecDoesNotExist() {
        when(itemSpecRegistry.getEquipmentSpec(EQUIPMENT_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCreator.createEquipment(EQUIPMENT_NAME, gamePlayer))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("The item spec registry does not contain an equipment specification by the name 'Frag Grenade'");
    }

    @Test
    @DisplayName("createEquipment returns equipment instance based on given name")
    void createEquipment_validName() {
        EquipmentSpec equipmentSpec = new EquipmentSpec();
        Equipment equipment = mock(Equipment.class);

        when(itemSpecRegistry.getEquipmentSpec(EQUIPMENT_NAME)).thenReturn(Optional.of(equipmentSpec));
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        Equipment createdEquipment = itemCreator.createEquipment(EQUIPMENT_NAME, gamePlayer);

        assertThat(createdEquipment).isEqualTo(equipment);
    }

    @Test
    @DisplayName("createGun throws ItemNotFoundException when no gun specification exists by given name")
    void createGun_gunSpecDoesNotExist() {
        when(itemSpecRegistry.getGunSpec(GUN_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCreator.createGun(GUN_NAME, gamePlayer))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("The item spec registry does not contain a gun specification by the name 'MP5'");
    }

    @Test
    @DisplayName("createGun returns gun instance based on given name")
    void createGun_validName() {
        GunSpec gunSpec = new GunSpec();
        Gun gun = mock(Gun.class);

        when(itemSpecRegistry.getGunSpec(GUN_NAME)).thenReturn(Optional.of(gunSpec));
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(gun);

        Gun createdGun = itemCreator.createGun(GUN_NAME, gamePlayer);

        assertThat(createdGun).isEqualTo(gun);
    }

    @Test
    @DisplayName("createMeleeWeapon throws ItemNotFoundException when no melee weapon specification exists by given name")
    void createMeleeWeapon_meleeWeaponSpecDoesNotExist() {
        when(itemSpecRegistry.getMeleeWeaponSpec(MELEE_WEAPON_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCreator.createMeleeWeapon(MELEE_WEAPON_NAME, gamePlayer))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("The item spec registry does not contain a melee weapon specification by the name 'Combat Knife'");
    }

    @Test
    @DisplayName("createMeleeWeapon returns melee weapon instance based on given name")
    void createMeleeWeapon_validName() {
        MeleeWeaponSpec meleeWeaponSpec = new MeleeWeaponSpec();
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);

        when(itemSpecRegistry.getMeleeWeaponSpec(MELEE_WEAPON_NAME)).thenReturn(Optional.of(meleeWeaponSpec));
        when(meleeWeaponFactory.create(meleeWeaponSpec)).thenReturn(meleeWeapon);

        MeleeWeapon createdMeleeWeapon = itemCreator.createMeleeWeapon(MELEE_WEAPON_NAME, gamePlayer);

        assertThat(createdMeleeWeapon).isEqualTo(meleeWeapon);

        verify(meleeWeapon).assign(gamePlayer);
    }

    @Test
    @DisplayName("createWeapon returns equipment instance based on given equipment name")
    void createWeapon_returnsEquipment() {
        EquipmentSpec equipmentSpec = new EquipmentSpec();
        Equipment equipment = mock(Equipment.class);

        when(itemSpecRegistry.getEquipmentSpec(EQUIPMENT_NAME)).thenReturn(Optional.of(equipmentSpec));
        when(equipmentFactory.create(equipmentSpec, gamePlayer)).thenReturn(equipment);

        Weapon weapon = itemCreator.createWeapon(gamePlayer, EQUIPMENT_NAME);

        assertThat(weapon).isEqualTo(equipment);
    }

    @Test
    @DisplayName("createWeapon returns gun instance based on given gun name")
    void createWeapon_returnsGun() {
        GunSpec gunSpec = new GunSpec();
        Gun gun = mock(Gun.class);

        when(itemSpecRegistry.getGunSpec(GUN_NAME)).thenReturn(Optional.of(gunSpec));
        when(gunFactory.create(gunSpec, gamePlayer)).thenReturn(gun);

        Weapon weapon = itemCreator.createWeapon(gamePlayer, GUN_NAME);

        assertThat(weapon).isEqualTo(gun);
    }

    @Test
    @DisplayName("createWeapon returns melee weapon instance based on given melee weapon name")
    void createWeapon_returnsMeleeWeapon() {
        MeleeWeaponSpec meleeWeaponSpec = new MeleeWeaponSpec();
        MeleeWeapon meleeWeapon = mock(MeleeWeapon.class);

        when(itemSpecRegistry.getMeleeWeaponSpec(MELEE_WEAPON_NAME)).thenReturn(Optional.of(meleeWeaponSpec));
        when(meleeWeaponFactory.create(meleeWeaponSpec)).thenReturn(meleeWeapon);

        Weapon weapon = itemCreator.createWeapon(gamePlayer, MELEE_WEAPON_NAME);

        assertThat(weapon).isEqualTo(meleeWeapon);

        verify(meleeWeapon).assign(gamePlayer);
    }

    @Test
    @DisplayName("createWeapon throws ItemNotFoundException when given name matches none of the specifications")
    void createWeaponThrowsWeaponNotFoundExceptionWhenGivenNameMatchesNoneOfTheSpecifications() {
        when(itemSpecRegistry.getEquipmentSpec(GUN_NAME)).thenReturn(Optional.empty());
        when(itemSpecRegistry.getGunSpec(GUN_NAME)).thenReturn(Optional.empty());
        when(itemSpecRegistry.getEquipmentSpec(GUN_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemCreator.createWeapon(gamePlayer, GUN_NAME))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("The item spec registry does not contain a specification for the weapon 'MP5'");
    }
}
