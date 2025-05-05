package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.item.equipment.EquipmentFactory;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class WeaponCreatorTest {

    private EquipmentFactory equipmentFactory;
    private FirearmFactory firearmFactory;

    @BeforeEach
    public void setUp() {
        equipmentFactory = mock(EquipmentFactory.class);
        firearmFactory = mock(FirearmFactory.class);
    }

    @Test
    public void existsReturnsTrueWhenSpecificationOfGivenWeaponIdExists() {
        String gunId = "TEST_GUN";
        GunSpec gunSpec = this.createGunSpec();

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        weaponCreator.addGunSpec(gunId, gunSpec);
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseWhenSpecificationOfGivenWeaponIdDoesNotExist() {
        String gunId = "TEST_GUN";

        WeaponCreator weaponCreator = new WeaponCreator(equipmentFactory, firearmFactory);
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isFalse();
    }

    private GunSpec createGunSpec() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);
        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpec controlsSpec = new ControlsSpec("reload", "shoot", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("test", null, null, null);
        RecoilSpec recoilSpec = new RecoilSpec("recoil type", List.of(), List.of(), null, null, null);
        ScopeSpec scopeSpec = new ScopeSpec(List.of(-0.1f, -0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("pattern type", 1, 0.5f, 0.5f);

        return new GunSpec("test", null, 1, 1, 1, rangeProfileSpec, 1.0, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, recoilSpec, scopeSpec, spreadPatternSpec);
    }
}
