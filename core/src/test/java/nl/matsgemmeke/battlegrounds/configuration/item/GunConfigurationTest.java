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
        when(yamlReader.getAsOptional("name", String.class)).thenReturn(Optional.empty());

        GunConfiguration configuration = new GunConfiguration(yamlReader);

        assertThatThrownBy(configuration::createSpec)
                .isInstanceOf(InvalidItemConfigurationException.class)
                .hasMessage("Missing required 'name' value");
    }

    @Test
    public void createSpecReturnsGunSpecContainingValuesFromYamlReader() {
        String name = "Test Gun";

        int magazineSize = 10;
        int maxMagazineAmount = 5;
        int defaultMagazineAmount = 3;

        double shortRangeDamage = 35.0;
        double shortRangeDistance = 10.0;
        double mediumRangeDamage = 25.0;
        double mediumRangeDistance = 20.0;
        double longRangeDamage = 15.0;
        double longRangeDistance = 30.0;
        double headshotDamageMultiplier = 2.0;

        String itemMaterial = "IRON_HOE";
        String itemDisplayName = "Test Gun %magazine_ammo%";
        int itemDamage = 1;

        String reloadAction = "LEFT_CLICK";
        String shootAction = "RIGHT_CLICK";

        String fireModeType = "FULLY_AUTOMATIC";
        int rateOfFire = 600;

        String recoilType = "CAMERA_MOVEMENT";
        List<Float> horizontalRecoilValues = List.of(0.1f);
        List<Float> verticalRecoilValues = List.of(0.2f);

        String spreadPatternType = "BUCKSHOT";
        int projectileAmount = 3;
        float horizontalSpread = 0.4f;
        float verticalSpread = 0.5f;

        when(yamlReader.getAsOptional("name", String.class)).thenReturn(Optional.of(name));
        when(yamlReader.getAsOptional("description", String.class)).thenReturn(Optional.empty());

        when(yamlReader.getAsOptional("ammo.magazine-size", Integer.class)).thenReturn(Optional.of(magazineSize));
        when(yamlReader.getAsOptional("ammo.max-magazine-amount", Integer.class)).thenReturn(Optional.of(maxMagazineAmount));
        when(yamlReader.getAsOptional("ammo.default-supply", Integer.class)).thenReturn(Optional.of(defaultMagazineAmount));

        when(yamlReader.getAsOptional("shooting.range.short-range.damage", Double.class)).thenReturn(Optional.of(shortRangeDamage));
        when(yamlReader.getAsOptional("shooting.range.short-range.distance", Double.class)).thenReturn(Optional.of(shortRangeDistance));
        when(yamlReader.getAsOptional("shooting.range.medium-range.damage", Double.class)).thenReturn(Optional.of(mediumRangeDamage));
        when(yamlReader.getAsOptional("shooting.range.medium-range.distance", Double.class)).thenReturn(Optional.of(mediumRangeDistance));
        when(yamlReader.getAsOptional("shooting.range.long-range.damage", Double.class)).thenReturn(Optional.of(longRangeDamage));
        when(yamlReader.getAsOptional("shooting.range.long-range.distance", Double.class)).thenReturn(Optional.of(longRangeDistance));
        when(yamlReader.getAsOptional("shooting.headshot-damage-multiplier", Double.class)).thenReturn(Optional.of(headshotDamageMultiplier));

        when(yamlReader.getAsOptional("shooting.shot-sound", String.class)).thenReturn(Optional.empty());

        when(yamlReader.getAsOptional("item.material", String.class)).thenReturn(Optional.of(itemMaterial));
        when(yamlReader.getAsOptional("item.display-name", String.class)).thenReturn(Optional.of(itemDisplayName));
        when(yamlReader.getAsOptional("item.damage", Integer.class)).thenReturn(Optional.of(itemDamage));

        when(yamlReader.getAsOptional("controls.reload", String.class)).thenReturn(Optional.of(reloadAction));
        when(yamlReader.getAsOptional("controls.shoot", String.class)).thenReturn(Optional.of(shootAction));
        when(yamlReader.getAsOptional("controls.use-scope", String.class)).thenReturn(Optional.empty());
        when(yamlReader.getAsOptional("controls.stop-scope", String.class)).thenReturn(Optional.empty());
        when(yamlReader.getAsOptional("controls.change-scope-magnification", String.class)).thenReturn(Optional.empty());

        when(yamlReader.getAsOptional("shooting.fire-mode.type", String.class)).thenReturn(Optional.of(fireModeType));
        when(yamlReader.getAsOptional("shooting.fire-mode.amount-of-shots", Integer.class)).thenReturn(Optional.empty());
        when(yamlReader.getAsOptional("shooting.fire-mode.rate-of-fire", Integer.class)).thenReturn(Optional.of(rateOfFire));
        when(yamlReader.getAsOptional("shooting.fire-mode.delay-between-shots", Long.class)).thenReturn(Optional.empty());

        when(yamlReader.contains("shooting.recoil")).thenReturn(true);
        when(yamlReader.getAsOptional("shooting.recoil.type", String.class)).thenReturn(Optional.of(recoilType));
        when(yamlReader.getOptionalFloatList("shooting.recoil.horizontal")).thenReturn(Optional.of(horizontalRecoilValues));
        when(yamlReader.getOptionalFloatList("shooting.recoil.vertical")).thenReturn(Optional.of(verticalRecoilValues));
        when(yamlReader.getAsOptional("shooting.recoil.kickback-duration", Long.class)).thenReturn(Optional.empty());
        when(yamlReader.getAsOptional("shooting.recoil.recovery-rate", Float.class)).thenReturn(Optional.empty());
        when(yamlReader.getAsOptional("shooting.recoil.recovery-duration", Long.class)).thenReturn(Optional.empty());

        when(yamlReader.contains("shooting.spread-pattern")).thenReturn(true);
        when(yamlReader.getAsOptional("shooting.spread-pattern.type", String.class)).thenReturn(Optional.of(spreadPatternType));
        when(yamlReader.getAsOptional("shooting.spread-pattern.projectile-amount", Integer.class)).thenReturn(Optional.of(projectileAmount));
        when(yamlReader.getAsOptional("shooting.spread-pattern.horizontal-spread", Float.class)).thenReturn(Optional.of(horizontalSpread));
        when(yamlReader.getAsOptional("shooting.spread-pattern.vertical-spread", Float.class)).thenReturn(Optional.of(verticalSpread));

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
