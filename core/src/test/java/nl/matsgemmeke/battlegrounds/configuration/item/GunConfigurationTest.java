package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GunConfigurationTest {

    private YamlReader yamlReader;

    @BeforeEach
    public void setUp() {
        yamlReader = mock(YamlReader.class);
    }

    @Test
    public void createSpecThrowsInvalidItemConfigurationExceptionWhenValueFromYamlDoesNotPassValidator() {
        when(yamlReader.getString("name")).thenReturn(null);

        GunConfiguration configuration = new GunConfiguration(yamlReader);

        assertThatThrownBy(configuration::createSpec)
                .isInstanceOf(InvalidItemConfigurationException.class)
                .hasMessage("Missing required value at 'name'");
    }

    @Test
    public void createSpecReturnsGunSpecContainingValuesFromYamlReader() {
        String name = "Test Gun";

        Integer magazineSize = 10;
        Integer maxMagazineAmount = 5;
        Integer defaultMagazineAmount = 3;

        Double shortRangeDamage = 35.0;
        Double shortRangeDistance = 10.0;
        Double mediumRangeDamage = 25.0;
        Double mediumRangeDistance = 20.0;
        Double longRangeDamage = 15.0;
        Double longRangeDistance = 30.0;
        Double headshotDamageMultiplier = 2.0;

        String reloadType = "MAGAZINE";
        Long reloadDuration = 50L;

        String itemMaterial = "IRON_HOE";
        String itemDisplayName = "Test Gun %magazine_ammo%";
        Integer itemDamage = 1;

        String reloadAction = "LEFT_CLICK";
        String shootAction = "RIGHT_CLICK";

        String fireModeType = "FULLY_AUTOMATIC";
        Integer rateOfFire = 600;

        String recoilType = "RANDOM_SPREAD";
        List<Float> horizontalRecoilValues = List.of(0.1f);
        List<Float> verticalRecoilValues = List.of(0.2f);

        String spreadPatternType = "BUCKSHOT";
        Integer projectileAmount = 3;
        Float horizontalSpread = 0.4f;
        Float verticalSpread = 0.5f;

        when(yamlReader.getString("name")).thenReturn(name);
        when(yamlReader.getString("description")).thenReturn(null);

        when(yamlReader.getOptionalInt("ammo.magazine-size")).thenReturn(Optional.of(magazineSize));
        when(yamlReader.getOptionalInt("ammo.max-magazine-amount")).thenReturn(Optional.of(maxMagazineAmount));
        when(yamlReader.getOptionalInt("ammo.default-supply")).thenReturn(Optional.of(defaultMagazineAmount));

        when(yamlReader.getDouble("shooting.range.short-range.damage")).thenReturn(shortRangeDamage);
        when(yamlReader.getDouble("shooting.range.short-range.distance")).thenReturn(shortRangeDistance);
        when(yamlReader.getDouble("shooting.range.medium-range.damage")).thenReturn(mediumRangeDamage);
        when(yamlReader.getDouble("shooting.range.medium-range.distance")).thenReturn(mediumRangeDistance);
        when(yamlReader.getDouble("shooting.range.long-range.damage")).thenReturn(longRangeDamage);
        when(yamlReader.getDouble("shooting.range.long-range.distance")).thenReturn(longRangeDistance);
        when(yamlReader.getDouble("shooting.headshot-damage-multiplier")).thenReturn(headshotDamageMultiplier);

        when(yamlReader.getString("shooting.shot-sounds")).thenReturn(null);

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

        when(yamlReader.getString("shooting.fire-mode.type")).thenReturn(fireModeType);
        when(yamlReader.getOptionalInt("shooting.fire-mode.amount-of-shots")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalInt("shooting.fire-mode.rate-of-fire")).thenReturn(Optional.of(rateOfFire));
        when(yamlReader.getOptionalLong("shooting.fire-mode.delay-between-shots")).thenReturn(Optional.empty());

        when(yamlReader.contains("shooting.recoil")).thenReturn(true);
        when(yamlReader.getString("shooting.recoil.type")).thenReturn(recoilType);
        when(yamlReader.getOptionalFloatList("shooting.recoil.horizontal")).thenReturn(Optional.of(horizontalRecoilValues));
        when(yamlReader.getOptionalFloatList("shooting.recoil.vertical")).thenReturn(Optional.of(verticalRecoilValues));
        when(yamlReader.getOptionalLong("shooting.recoil.kickback-duration")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalFloat("shooting.recoil.recovery-rate")).thenReturn(Optional.empty());
        when(yamlReader.getOptionalLong("shooting.recoil.recovery-duration")).thenReturn(Optional.empty());

        when(yamlReader.contains("shooting.spread-pattern")).thenReturn(true);
        when(yamlReader.getString("shooting.spread-pattern.type")).thenReturn(spreadPatternType);
        when(yamlReader.getOptionalInt("shooting.spread-pattern.projectile-amount")).thenReturn(Optional.of(projectileAmount));
        when(yamlReader.getOptionalFloat("shooting.spread-pattern.horizontal-spread")).thenReturn(Optional.of(horizontalSpread));
        when(yamlReader.getOptionalFloat("shooting.spread-pattern.vertical-spread")).thenReturn(Optional.of(verticalSpread));

        GunConfiguration configuration = new GunConfiguration(yamlReader);
        GunSpecification spec = configuration.createSpec();

        assertThat(spec.name()).isEqualTo(name);
        assertThat(spec.description()).isNull();

        assertThat(spec.magazineSize()).isEqualTo(magazineSize);
        assertThat(spec.maxMagazineAmount()).isEqualTo(maxMagazineAmount);
        assertThat(spec.defaultMagazineAmount()).isEqualTo(defaultMagazineAmount);

        assertThat(spec.shortRangeDamage()).isEqualTo(shortRangeDamage);
        assertThat(spec.shortRangeDistance()).isEqualTo(shortRangeDistance);
        assertThat(spec.mediumRangeDamage()).isEqualTo(mediumRangeDamage);
        assertThat(spec.mediumRangeDistance()).isEqualTo(mediumRangeDistance);
        assertThat(spec.longRangeDamage()).isEqualTo(longRangeDamage);
        assertThat(spec.longRangeDistance()).isEqualTo(longRangeDistance);
        assertThat(spec.headshotDamageMultiplier()).isEqualTo(headshotDamageMultiplier);

        assertThat(spec.shotSounds()).isNull();

        assertThat(spec.reloadSpec().type()).isEqualTo(reloadType);
        assertThat(spec.reloadSpec().reloadSounds()).isNull();
        assertThat(spec.reloadSpec().duration()).isEqualTo(reloadDuration);

        assertThat(spec.itemSpec().material()).isEqualTo(itemMaterial);
        assertThat(spec.itemSpec().displayName()).isEqualTo(itemDisplayName);
        assertThat(spec.itemSpec().damage()).isEqualTo(itemDamage);

        assertThat(spec.controls().reloadAction()).isEqualTo(reloadAction);
        assertThat(spec.controls().shootAction()).isEqualTo(shootAction);
        assertThat(spec.controls().useScopeAction()).isNull();
        assertThat(spec.controls().stopScopeAction()).isNull();
        assertThat(spec.controls().changeScopeMagnificationAction()).isNull();

        assertThat(spec.fireModeSpec().type()).isEqualTo(fireModeType);
        assertThat(spec.fireModeSpec().amountOfShots()).isNull();
        assertThat(spec.fireModeSpec().rateOfFire()).isEqualTo(rateOfFire);
        assertThat(spec.fireModeSpec().delayBetweenShots()).isNull();

        assertThat(spec.recoil()).isNotNull();
        assertThat(spec.recoil().type()).isEqualTo(recoilType);
        assertThat(spec.recoil().horizontalRecoilValues()).isEqualTo(horizontalRecoilValues);
        assertThat(spec.recoil().verticalRecoilValues()).isEqualTo(verticalRecoilValues);
        assertThat(spec.recoil().kickbackDuration()).isNull();
        assertThat(spec.recoil().recoveryRate()).isEqualTo(0.0f);
        assertThat(spec.recoil().recoveryDuration()).isEqualTo(0L);

        assertThat(spec.spreadPattern()).isNotNull();
        assertThat(spec.spreadPattern().type()).isEqualTo(spreadPatternType);
        assertThat(spec.spreadPattern().projectileAmount()).isEqualTo(projectileAmount);
        assertThat(spec.spreadPattern().horizontalSpread()).isEqualTo(horizontalSpread);
        assertThat(spec.spreadPattern().verticalSpread()).isEqualTo(verticalSpread);
    }
}
