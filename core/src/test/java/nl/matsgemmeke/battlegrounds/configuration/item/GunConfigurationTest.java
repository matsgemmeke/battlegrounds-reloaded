package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        when(yamlReader.getInt("ammo.magazine-size")).thenReturn(magazineSize);
        when(yamlReader.getInt("ammo.max-magazine-amount")).thenReturn(maxMagazineAmount);
        when(yamlReader.getInt("ammo.default-supply")).thenReturn(defaultMagazineAmount);

        when(yamlReader.getDouble("shooting.range.short-range.damage")).thenReturn(shortRangeDamage);
        when(yamlReader.getDouble("shooting.range.short-range.distance")).thenReturn(shortRangeDistance);
        when(yamlReader.getDouble("shooting.range.medium-range.damage")).thenReturn(mediumRangeDamage);
        when(yamlReader.getDouble("shooting.range.medium-range.distance")).thenReturn(mediumRangeDistance);
        when(yamlReader.getDouble("shooting.range.long-range.damage")).thenReturn(longRangeDamage);
        when(yamlReader.getDouble("shooting.range.long-range.distance")).thenReturn(longRangeDistance);
        when(yamlReader.getDouble("shooting.headshot-damage-multiplier")).thenReturn(headshotDamageMultiplier);

        when(yamlReader.getString("shooting.shot-sounds")).thenReturn(null);

        when(yamlReader.getString("item.material")).thenReturn(itemMaterial);
        when(yamlReader.getString("item.display-name")).thenReturn(itemDisplayName);
        when(yamlReader.getInt("item.damage")).thenReturn(itemDamage);

        when(yamlReader.getString("controls.reload")).thenReturn(reloadAction);
        when(yamlReader.getString("controls.shoot")).thenReturn(shootAction);
        when(yamlReader.getString("controls.scope-use")).thenReturn(null);
        when(yamlReader.getString("controls.scope-stop")).thenReturn(null);
        when(yamlReader.getString("controls.scope-change-magnification")).thenReturn(null);

        when(yamlReader.getString("shooting.fire-mode.type")).thenReturn(fireModeType);
        when(yamlReader.getInt("shooting.fire-mode.amount-of-shots")).thenReturn(null);
        when(yamlReader.getInt("shooting.fire-mode.rate-of-fire")).thenReturn(rateOfFire);
        when(yamlReader.getLong("shooting.fire-mode.delay-between-shots")).thenReturn(null);

        when(yamlReader.contains("shooting.recoil")).thenReturn(true);
        when(yamlReader.getString("shooting.recoil.type")).thenReturn(recoilType);
        when(yamlReader.getFloatList("shooting.recoil.horizontal")).thenReturn(horizontalRecoilValues);
        when(yamlReader.getFloatList("shooting.recoil.vertical")).thenReturn(verticalRecoilValues);
        when(yamlReader.getLong("shooting.recoil.kickback-duration")).thenReturn(null);
        when(yamlReader.getFloat("shooting.recoil.recovery-rate")).thenReturn(null);
        when(yamlReader.getLong("shooting.recoil.recovery-duration")).thenReturn(null);

        when(yamlReader.contains("shooting.spread-pattern")).thenReturn(true);
        when(yamlReader.getString("shooting.spread-pattern.type")).thenReturn(spreadPatternType);
        when(yamlReader.getInt("shooting.spread-pattern.projectile-amount")).thenReturn(projectileAmount);
        when(yamlReader.getFloat("shooting.spread-pattern.horizontal-spread")).thenReturn(horizontalSpread);
        when(yamlReader.getFloat("shooting.spread-pattern.vertical-spread")).thenReturn(verticalSpread);

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

        assertThat(spec.item().material()).isEqualTo(itemMaterial);
        assertThat(spec.item().displayName()).isEqualTo(itemDisplayName);
        assertThat(spec.item().damage()).isEqualTo(itemDamage);

        assertThat(spec.controls().reloadAction()).isEqualTo(reloadAction);
        assertThat(spec.controls().shootAction()).isEqualTo(shootAction);
        assertThat(spec.controls().useScopeAction()).isNull();
        assertThat(spec.controls().stopScopeAction()).isNull();
        assertThat(spec.controls().changeScopeMagnificationAction()).isNull();

        assertThat(spec.fireMode().type()).isEqualTo(fireModeType);
        assertThat(spec.fireMode().amountOfShots()).isNull();
        assertThat(spec.fireMode().rateOfFire()).isEqualTo(rateOfFire);
        assertThat(spec.fireMode().delayBetweenShots()).isNull();

        assertThat(spec.recoil()).isNotNull();
        assertThat(spec.recoil().type()).isEqualTo(recoilType);
        assertThat(spec.recoil().horizontalRecoilValues()).isEqualTo(horizontalRecoilValues);
        assertThat(spec.recoil().verticalRecoilValues()).isEqualTo(verticalRecoilValues);
        assertThat(spec.recoil().kickbackDuration()).isNull();
        assertThat(spec.recoil().recoveryRate()).isNull();
        assertThat(spec.recoil().recoveryDuration()).isNull();

        assertThat(spec.spreadPattern()).isNotNull();
        assertThat(spec.spreadPattern().type()).isEqualTo(spreadPatternType);
        assertThat(spec.spreadPattern().projectileAmount()).isEqualTo(projectileAmount);
        assertThat(spec.spreadPattern().horizontalSpread()).isEqualTo(horizontalSpread);
        assertThat(spec.spreadPattern().verticalSpread()).isEqualTo(verticalSpread);
    }
}
