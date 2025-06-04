package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Firearm;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WeaponCreatorTest {

    private static final String GUN_ID = "TEST_GUN";

    private EquipmentFactory equipmentFactory;
    private FirearmFactory firearmFactory;

    @BeforeEach
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
    }

    @Test
    public void createGunThrowsWeaponNotFoundExceptionWhenNoGunSpecsExistByGivenGunId() {
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GameKey gameKey = GameKey.ofOpenMode();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);

        assertThatThrownBy(() -> weaponCreator.createGun("fail", gamePlayer, gameKey))
                .isInstanceOf(WeaponNotFoundException.class)
                .hasMessage("The weapon creator does not contain a specification for a gun by the id 'fail'");
    }

    @Test
    public void createGunReturnsGunInstanceBasedOnGivenGunId() {
        GunSpec gunSpec = this.createGunSpec(GUN_ID);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        GameKey gameKey = GameKey.ofOpenMode();

        Firearm firearm = mock(Firearm.class);
        when(firearmFactory.create(gunSpec, gameKey, gamePlayer)).thenReturn(firearm);

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(GUN_ID, gunSpec);
        Gun gun = weaponCreator.createGun(GUN_ID, gamePlayer, gameKey);

        assertThat(gun).isEqualTo(firearm);
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
        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("reload", "shoot", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("test", null, null, null);
        RecoilSpec recoilSpec = new RecoilSpec("recoil type", List.of(), List.of(), null, null, null);
        ScopeSpec scopeSpec = new ScopeSpec(List.of(-0.1f, -0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("pattern type", 1, 0.5f, 0.5f);

        return new GunSpec(gunId, "Test Gun", null, 1, 1, 1, rangeProfileSpec, 1.0, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, recoilSpec, scopeSpec, spreadPatternSpec);
    }
}
