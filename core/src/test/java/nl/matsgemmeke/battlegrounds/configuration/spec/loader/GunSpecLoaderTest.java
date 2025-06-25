package nl.matsgemmeke.battlegrounds.configuration.spec.loader;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.item.shoot.*;
import nl.matsgemmeke.battlegrounds.configuration.spec.InvalidFieldSpecException;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GunSpecLoaderTest {

    private RangeProfileSpecLoader rangeProfileSpecLoader;
    private ShootingSpecLoader shootingSpecLoader;
    private YamlReader yamlReader;

    @BeforeEach
    public void setUp() {
        rangeProfileSpecLoader = mock(RangeProfileSpecLoader.class);
        shootingSpecLoader = mock(ShootingSpecLoader.class);
        yamlReader = mock(YamlReader.class);
    }

    @Test
    public void createSpecThrowsInvalidFieldSpecExceptionWhenValueFromYamlDoesNotPassValidator() {
        when(yamlReader.getString("id")).thenReturn(null);

        GunSpecLoader specLoader = new GunSpecLoader(yamlReader, rangeProfileSpecLoader, shootingSpecLoader);

        assertThatThrownBy(specLoader::loadSpec)
                .isInstanceOf(InvalidFieldSpecException.class)
                .hasMessage("Missing required value at 'id'");
    }

    @Test
    public void createSpecReturnsGunSpecContainingValuesFromYamlReader() {
        String id = "TEST_GUN";
        String name = "Test Gun";

        Integer magazineSize = 10;
        Integer maxMagazineAmount = 5;
        Integer defaultMagazineAmount = 3;

        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        Double headshotDamageMultiplier = 2.0;

        FireModeSpec fireModeSpec = new FireModeSpec("FULLY_AUTOMATIC", null, 600, null);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        RecoilSpec recoilSpec = new RecoilSpec("RANDOM_SPREAD", List.of(0.1f), List.of(0.2f), null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("SINGLE_PROJECTILE", null, null, null);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, recoilSpec, spreadPatternSpec, null);

        String reloadType = "MAGAZINE";
        Long reloadDuration = 50L;

        String itemMaterial = "IRON_HOE";
        String itemDisplayName = "Test Gun %magazine_ammo%";
        Integer itemDamage = 1;

        String reloadAction = "LEFT_CLICK";
        String shootAction = "RIGHT_CLICK";

        List<Float> magnifications = List.of(-0.1f, -0.2f);

        when(yamlReader.getString("id")).thenReturn(id);
        when(yamlReader.getString("name")).thenReturn(name);
        when(yamlReader.getString("description")).thenReturn(null);

        when(yamlReader.getOptionalInt("ammo.magazine-size")).thenReturn(Optional.of(magazineSize));
        when(yamlReader.getOptionalInt("ammo.max-magazine-amount")).thenReturn(Optional.of(maxMagazineAmount));
        when(yamlReader.getOptionalInt("ammo.default-supply")).thenReturn(Optional.of(defaultMagazineAmount));

        when(rangeProfileSpecLoader.loadSpec("shooting.range")).thenReturn(rangeProfileSpec);

        when(yamlReader.getDouble("shooting.headshot-damage-multiplier")).thenReturn(headshotDamageMultiplier);

        when(shootingSpecLoader.loadSpec("shooting")).thenReturn(shootingSpec);

        when(yamlReader.getString("reloading.type")).thenReturn(reloadType);
        when(yamlReader.getString("reloading.reload-sounds")).thenReturn(null);
        when(yamlReader.getOptionalLong("reloading.duration")).thenReturn(Optional.of(reloadDuration));

        when(yamlReader.getString("item.material")).thenReturn(itemMaterial);
        when(yamlReader.getString("item.display-name")).thenReturn(itemDisplayName);
        when(yamlReader.getOptionalInt("item.damage")).thenReturn(Optional.of(itemDamage));

        when(yamlReader.getString("controls.reload")).thenReturn(reloadAction);
        when(yamlReader.getString("controls.shoot")).thenReturn(shootAction);
        when(yamlReader.getString("controls.scope-use")).thenReturn(null);
        when(yamlReader.getString("controls.scope-stop")).thenReturn(null);
        when(yamlReader.getString("controls.scope-change-magnification")).thenReturn(null);

        when(yamlReader.contains("scope")).thenReturn(true);
        when(yamlReader.getOptionalFloatList("scope.magnifications")).thenReturn(Optional.of(magnifications));
        when(yamlReader.getString("scope.use-sounds")).thenReturn(null);
        when(yamlReader.getString("scope.stop-sounds")).thenReturn(null);
        when(yamlReader.getString("scope.change-magnification-sounds")).thenReturn(null);

        GunSpecLoader specLoader = new GunSpecLoader(yamlReader, rangeProfileSpecLoader, shootingSpecLoader);
        GunSpec spec = specLoader.loadSpec();

        assertThat(spec.id()).isEqualTo(id);
        assertThat(spec.name()).isEqualTo(name);
        assertThat(spec.description()).isNull();

        assertThat(spec.magazineSize()).isEqualTo(magazineSize);
        assertThat(spec.maxMagazineAmount()).isEqualTo(maxMagazineAmount);
        assertThat(spec.defaultMagazineAmount()).isEqualTo(defaultMagazineAmount);

        assertThat(spec.rangeProfile()).isEqualTo(rangeProfileSpec);

        assertThat(spec.headshotDamageMultiplier()).isEqualTo(headshotDamageMultiplier);

        assertThat(spec.shooting()).isEqualTo(shootingSpec);

        assertThat(spec.reload().type()).isEqualTo(reloadType);
        assertThat(spec.reload().reloadSounds()).isNull();
        assertThat(spec.reload().duration()).isEqualTo(reloadDuration);

        assertThat(spec.item().material()).isEqualTo(itemMaterial);
        assertThat(spec.item().displayName()).isEqualTo(itemDisplayName);
        assertThat(spec.item().damage()).isEqualTo(itemDamage);

        assertThat(spec.controls().reloadAction()).isEqualTo(reloadAction);
        assertThat(spec.controls().shootAction()).isEqualTo(shootAction);
        assertThat(spec.controls().useScopeAction()).isNull();
        assertThat(spec.controls().stopScopeAction()).isNull();
        assertThat(spec.controls().changeScopeMagnificationAction()).isNull();

        assertThat(spec.scope()).isNotNull();
        assertThat(spec.scope().magnifications()).isEqualTo(magnifications);
        assertThat(spec.scope().useSounds()).isNull();
        assertThat(spec.scope().stopSounds()).isNull();
        assertThat(spec.scope().changeMagnificationSounds()).isNull();
    }
}
