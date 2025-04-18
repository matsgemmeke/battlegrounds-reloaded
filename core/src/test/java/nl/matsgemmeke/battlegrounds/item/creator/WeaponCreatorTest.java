package nl.matsgemmeke.battlegrounds.item.creator;

import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.item.gun.FirearmFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class WeaponCreatorTest {

    private FirearmFactory firearmFactory;

    @BeforeEach
    public void setUp() {
        firearmFactory = mock(FirearmFactory.class);
    }

    @Test
    public void existsReturnsTrueWhenSpecificationOfGivenWeaponIdExists() {
        String gunId = "TEST_GUN";
        GunSpecification gunSpecification = this.createGunSpecification();

        WeaponCreator weaponCreator = new WeaponCreator(firearmFactory);
        weaponCreator.addGunSpecification(gunId, gunSpecification);
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isTrue();
    }

    @Test
    public void existsReturnsFalseWhenSpecificationOfGivenWeaponIdDoesNotExist() {
        String gunId = "TEST_GUN";

        WeaponCreator weaponCreator = new WeaponCreator(firearmFactory);
        boolean exists = weaponCreator.exists(gunId);

        assertThat(exists).isFalse();
    }

    private GunSpecification createGunSpecification() {
        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("STICK", "name", 1);
        ControlsSpecification controls = new ControlsSpecification("reload", "shoot", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("test", null, null, null);
        RecoilSpec recoilSpec = new RecoilSpec("recoil type", List.of(), List.of(), null, null, null);
        SpreadPatternSpecification spreadPattern = new SpreadPatternSpecification("pattern type", 1, 0.5f, 0.5f);

        return new GunSpecification("test", null, 1, 1, 1, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, null, reloadSpec, itemSpec, controls, fireModeSpec, recoilSpec, spreadPattern);
    }
}
