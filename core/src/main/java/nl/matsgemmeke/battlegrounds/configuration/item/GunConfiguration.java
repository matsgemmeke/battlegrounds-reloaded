package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.FieldSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpecification;
import nl.matsgemmeke.battlegrounds.configuration.validation.RequiredValidator;
import nl.matsgemmeke.battlegrounds.configuration.validation.ValidationResult;
import nl.matsgemmeke.battlegrounds.configuration.validation.Validator;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class GunConfiguration {

    @NotNull
    private final YamlReader yamlReader;

    public GunConfiguration(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public GunSpecification createSpec() {
        String name = this.resolve(String.class, "name", new RequiredValidator<>());
        String description = this.resolve(String.class, "description");

        int magazineSize = this.resolve(Integer.class, "ammo.magazine-size", new RequiredValidator<>());
        int maxMagazineAmount = this.resolve(Integer.class, "ammo.max-magazine-amount", new RequiredValidator<>());
        int defaultMagazineAmount = this.resolve(Integer.class, "ammo.default-supply", new RequiredValidator<>());

        double shortRangeDamage = this.resolve(Double.class, "shooting.range.short-range.damage", new RequiredValidator<>());
        double shortRangeDistance = this.resolve(Double.class, "shooting.range.short-range.distance", new RequiredValidator<>());
        double mediumRangeDamage = this.resolve(Double.class, "shooting.range.medium-range.damage", new RequiredValidator<>());
        double mediumRangeDistance = this.resolve(Double.class, "shooting.range.medium-range.distance", new RequiredValidator<>());
        double longRangeDamage = this.resolve(Double.class, "shooting.range.long-range.damage", new RequiredValidator<>());
        double longRangeDistance = this.resolve(Double.class, "shooting.range.long-range.distance", new RequiredValidator<>());
        double headshotDamageMultiplier = this.resolve(Double.class, "shooting.headshot-damage-multiplier", new RequiredValidator<>());

        String shotSounds = this.resolve(String.class, "shooting.shot-sound");

        String itemMaterial = this.resolve(String.class, "item.material", new RequiredValidator<>());
        String itemDisplayName = this.resolve(String.class, "item.display-name", new RequiredValidator<>());
        int itemDamage = this.resolve(Integer.class, "item.damage", new RequiredValidator<>());
        ItemStackSpecification item = new ItemStackSpecification(itemMaterial, itemDisplayName, itemDamage);

        String reloadAction = this.resolve(String.class, "controls.reload", new RequiredValidator<>());
        String shootAction = this.resolve(String.class, "controls.shoot", new RequiredValidator<>());
        String useScopeAction = this.resolve(String.class, "controls.use-scope");
        String stopScopeAction = this.resolve(String.class, "controls.stop-scope");
        String changeScopeMagnificationAction = this.resolve(String.class, "controls.change-scope-magnification");
        ControlsSpecification controls = new ControlsSpecification(reloadAction, shootAction, useScopeAction, stopScopeAction, changeScopeMagnificationAction);

        String fireModeType = this.resolve(String.class, "shooting.fire-mode.type", new RequiredValidator<>());
        Integer amountOfShots = this.resolve(Integer.class, "shooting.fire-mode.amount-of-shots");
        Integer rateOfFire = this.resolve(Integer.class, "shooting.fire-mode.rate-of-fire");
        Long delayBetweenShots = this.resolve(Long.class, "shooting.fire-mode.delay-between-shots");
        FireModeSpecification fireMode = new FireModeSpecification(fireModeType, amountOfShots, rateOfFire, delayBetweenShots);

        RecoilSpecification recoil = null;
        SpreadPatternSpecification spreadPattern = null;

        if (yamlReader.contains("shooting.recoil")) {
            String recoilType = this.resolve(String.class, "shooting.recoil.type", new RequiredValidator<>());
            List<Float> horizontalRecoilValues = yamlReader.getOptionalFloatList("shooting.recoil.horizontal").orElse(Collections.emptyList());
            List<Float> verticalRecoilValues = yamlReader.getOptionalFloatList("shooting.recoil.vertical").orElse(Collections.emptyList());
            Long kickbackDuration = this.resolve(Long.class, "shooting.recoil.kickback-duration");
            Float recoveryRate = this.resolve(Float.class, "shooting.recoil.recovery-rate");
            Long recoveryDuration = this.resolve(Long.class, "shooting.recoil.recovery-duration");

            recoil = new RecoilSpecification(recoilType, horizontalRecoilValues, verticalRecoilValues, kickbackDuration, recoveryRate, recoveryDuration);
        }

        if (yamlReader.contains("shooting.spread-pattern")) {
            String spreadPatternType = this.resolve(String.class, "shooting.spread-pattern.type", new RequiredValidator<>());
            Integer projectileAmount = this.resolve(Integer.class, "shooting.spread-pattern.projectile-amount", new RequiredValidator<>());
            Float horizontalSpread = this.resolve(Float.class, "shooting.spread-pattern.horizontal-spread", new RequiredValidator<>());
            Float verticalSpread = this.resolve(Float.class, "shooting.spread-pattern.vertical-spread", new RequiredValidator<>());

            spreadPattern = new SpreadPatternSpecification(spreadPatternType, projectileAmount, horizontalSpread, verticalSpread);
        }

        return new GunSpecification(name, description, magazineSize, maxMagazineAmount, defaultMagazineAmount, shortRangeDamage, shortRangeDistance, mediumRangeDamage, mediumRangeDistance, longRangeDamage, longRangeDistance, headshotDamageMultiplier, shotSounds, item, controls, fireMode, recoil, spreadPattern);
    }

    @SafeVarargs
    private <T> T resolve(@NotNull Class<T> type, @NotNull String route, @NotNull Validator<T>... validators) {
        FieldSpec<T> spec = new FieldSpec<>(route);
        Stream.of(validators).forEach(spec::withValidator);

        T configValue = yamlReader.getAsOptional(route, type).orElse(null);
        ValidationResult<T> result = spec.getValidatedValue(configValue);

        if (!result.isValid()) {
            throw new InvalidItemConfigurationException(result.getErrorMessage());
        }

        return result.getValue();
    }
}
