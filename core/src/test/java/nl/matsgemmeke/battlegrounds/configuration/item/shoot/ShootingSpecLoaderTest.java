package nl.matsgemmeke.battlegrounds.configuration.item.shoot;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShootingSpecLoaderTest {

    private static final String BASE_ROUTE = "base-route";

    @Test
    public void createSpecReturnsShootingSpecContainingValidatedValues() {
        String fireModeType = "FULLY_AUTOMATIC";
        int rateOfFire = 600;
        ProjectileSpec projectileSpec = new ProjectileSpec("BULLET", null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("BUCKSHOT", 5, 0.5f, 0.5f);

        String recoilType = "RANDOM_SPREAD";
        List<Float> horizontalRecoilValues = List.of(0.1f);
        List<Float> verticalRecoilValues = List.of(0.2f);

        YamlReader yamlReader = mock(YamlReader.class);
        when(yamlReader.getString("base-route.shot-sounds")).thenReturn(null);
        when(yamlReader.getString("base-route.fire-mode.type")).thenReturn(fireModeType);
        when(yamlReader.getOptionalInt("base-route.fire-mode.amount-of-shots")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalInt("base-route.fire-mode.cycle-cooldown")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalInt("base-route.fire-mode.rate-of-fire")).thenReturn(Optional.of(rateOfFire));

        when(yamlReader.contains("base-route.recoil")).thenReturn(true);
        when(yamlReader.getString("base-route.recoil.type")).thenReturn(recoilType);
        when(yamlReader.getOptionalFloatList("base-route.recoil.horizontal")).thenReturn(Optional.of(horizontalRecoilValues));
        when(yamlReader.getOptionalFloatList("base-route.recoil.vertical")).thenReturn(Optional.of(verticalRecoilValues));
        when(yamlReader.getOptionalLong("base-route.recoil.kickback-duration")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalFloat("base-route.recoil.recovery-rate")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalLong("base-route.recoil.recovery-duration")).thenReturn(Optional.empty());

        ProjectileSpecLoader projectileSpecLoader = mock(ProjectileSpecLoader.class);
        when(projectileSpecLoader.loadSpec("base-route.projectile")).thenReturn(projectileSpec);

        SpreadPatternSpecLoader spreadPatternSpecLoader = mock(SpreadPatternSpecLoader.class);
        when(spreadPatternSpecLoader.loadSpec("base-route.spread-pattern")).thenReturn(spreadPatternSpec);

        ShootingSpecLoader shootingSpecLoader = new ShootingSpecLoader(yamlReader, projectileSpecLoader, spreadPatternSpecLoader);
        ShootingSpec spec = shootingSpecLoader.loadSpec(BASE_ROUTE);

        assertThat(spec.shotSounds()).isNull();

        assertThat(spec.fireMode().type()).isEqualTo(fireModeType);
        assertThat(spec.fireMode().amountOfShots()).isNull();
        assertThat(spec.fireMode().cycleCooldown()).isNull();
        assertThat(spec.fireMode().rateOfFire()).isEqualTo(rateOfFire);

        assertThat(spec.projectile()).isEqualTo(projectileSpec);

        assertThat(spec.recoil()).isNotNull();
        assertThat(spec.recoil().type()).isEqualTo(recoilType);
        assertThat(spec.recoil().horizontalRecoilValues()).isEqualTo(horizontalRecoilValues);
        assertThat(spec.recoil().verticalRecoilValues()).isEqualTo(verticalRecoilValues);
        assertThat(spec.recoil().kickbackDuration()).isNull();
        assertThat(spec.recoil().recoveryRate()).isEqualTo(0.0f);
        assertThat(spec.recoil().recoveryDuration()).isEqualTo(0L);

        assertThat(spec.spreadPattern()).isEqualTo(spreadPatternSpec);
    }
}
