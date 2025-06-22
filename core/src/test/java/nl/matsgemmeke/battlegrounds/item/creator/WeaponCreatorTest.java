package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.equipment.EquipmentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.DeploymentSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.deploy.ProjectileEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.effect.ItemEffectSpec;
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

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeaponCreatorTest {

    private static final GameKey GAME_KEY = GameKey.ofOpenMode();
    private static final String EQUIPMENT_ID = "TEST_EQUIPMENT";
    private static final String GUN_ID = "TEST_GUN";

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
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(EQUIPMENT_ID);
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
        GunSpec gunSpec = this.createGunSpec(GUN_ID);
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
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(EQUIPMENT_ID);
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
        GunSpec gunSpec = this.createGunSpec(GUN_ID);
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
        GunSpec gunSpec = this.createGunSpec(GUN_ID);

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
        EquipmentSpec equipmentSpec = this.createEquipmentSpec(EQUIPMENT_ID);

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
        GunSpec gunSpec = this.createGunSpec(GUN_ID);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        boolean gunExists = weaponCreator.gunExists(GUN_ID);

        assertThat(gunExists).isTrue();
    }

    private GunSpec createGunSpec(String gunId) {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("reload", "shoot", null, null, null);
        RecoilSpec recoilSpec = new RecoilSpec("recoil type", List.of(), List.of(), null, null, null);
        ScopeSpec scopeSpec = new ScopeSpec(List.of(-0.1f, -0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("pattern type", 1, 0.5f, 0.5f);

        return new GunSpec(gunId, "Test Gun", null, 1, 1, 1, rangeProfileSpec, 1.0, shootingSpec, reloadSpec, itemSpec, controlsSpec, recoilSpec, scopeSpec, spreadPatternSpec);
    }

    private EquipmentSpec createEquipmentSpec(String equipmentId) {
        ItemStackSpec dislayItemSpec = new ItemStackSpec("STICK", "name", 1);
        nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec controlsSpec = new nl.matsgemmeke.battlegrounds.configuration.spec.equipment.ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null);
        DeploymentSpec deploymentSpec = new DeploymentSpec(20.0, false, false, false, false, null, Map.of(), null, null, null, null);
        ItemEffectSpec itemEffectSpec = new ItemEffectSpec("MARK_SPAWN_POINT", List.of(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        List<ProjectileEffectSpec> projectileEffectSpecs = List.of();

        return new EquipmentSpec(equipmentId, "Test Equipment", null, dislayItemSpec, null, null, controlsSpec, deploymentSpec, itemEffectSpec, projectileEffectSpecs);
    }
}
