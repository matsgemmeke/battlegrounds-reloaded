package nl.matsgemmeke.battlegrounds.configuration.item;

import nl.matsgemmeke.battlegrounds.configuration.YamlReader;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.FireModeSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ItemStackSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpecification;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpecification;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class GunConfiguration {

    @NotNull
    private final YamlReader yamlReader;

    public GunConfiguration(@NotNull YamlReader yamlReader) {
        this.yamlReader = yamlReader;
    }

    @NotNull
    public GunSpecification createSpec() {
        String name = this.getRequiredValue("name", String.class);
        String description = this.getOptionalValue("description", String.class);

        int magazineSize = this.getRequiredValue("ammo.magazine-size", Integer.class);
        int maxMagazineAmount = this.getRequiredValue("ammo.max-magazine-amount", Integer.class);
        int defaultMagazineAmount = this.getRequiredValue("ammo.default-supply", Integer.class);

        double shortRangeDamage = this.getRequiredValue("shooting.range.short-range.damage", Double.class);
        double shortRangeDistance = this.getRequiredValue("shooting.range.short-range.distance", Double.class);
        double mediumRangeDamage = this.getRequiredValue("shooting.range.medium-range.damage", Double.class);
        double mediumRangeDistance = this.getRequiredValue("shooting.range.medium-range.distance", Double.class);
        double longRangeDamage = this.getRequiredValue("shooting.range.long-range.damage", Double.class);
        double longRangeDistance = this.getRequiredValue("shooting.range.long-range.distance", Double.class);
        double headshotDamageMultiplier = this.getRequiredValue("shooting.headshot-damage-multiplier", Double.class);

        String shotSounds = this.getOptionalValue("shooting.shot-sound", String.class);

        Material itemMaterial = this.getMaterial("item.material");
        String itemDisplayName = this.getRequiredValue("item.display-name", String.class);
        int itemDamage = this.getRequiredValue("item.damage", Integer.class);
        ItemStackSpecification item = new ItemStackSpecification(itemMaterial, itemDisplayName, itemDamage);

        String reloadAction = this.getRequiredValue("controls.reload", String.class);
        String shootAction = this.getRequiredValue("controls.shoot", String.class);
        String useScopeAction = this.getOptionalValue("controls.use-scope", String.class);
        String stopScopeAction = this.getOptionalValue("controls.stop-scope", String.class);
        String changeScopeMagnificationAction = this.getOptionalValue("controls.change-scope-magnification", String.class);
        ControlsSpecification controls = new ControlsSpecification(reloadAction, shootAction, useScopeAction, stopScopeAction, changeScopeMagnificationAction);

        String fireModeType = this.getRequiredValue("shooting.fire-mode.type", String.class);
        Integer amountOfShots = this.getOptionalValue("shooting.fire-mode.amount-of-shots", Integer.class);
        Integer rateOfFire = this.getOptionalValue("shooting.fire-mode.rate-of-fire", Integer.class);
        Long delayBetweenShots = this.getOptionalValue("shooting.fire-mode.delay-between-shots", Long.class);
        FireModeSpecification fireMode = new FireModeSpecification(fireModeType, amountOfShots, rateOfFire, delayBetweenShots);

        RecoilSpecification recoil = null;
        SpreadPatternSpecification spreadPattern = null;

        if (yamlReader.contains("shooting.recoil")) {
            String recoilType = this.getRequiredValue("shooting.recoil.type", String.class);
            List<Float> horizontalRecoilValues = yamlReader.getOptionalFloatList("shooting.recoil.horizontal").orElse(Collections.emptyList());
            List<Float> verticalRecoilValues = yamlReader.getOptionalFloatList("shooting.recoil.vertical").orElse(Collections.emptyList());
            Long kickbackDuration = this.getOptionalValue("shooting.recoil.kickback-duration", Long.class);
            Float recoveryRate = this.getOptionalValue("shooting.recoil.recovery-rate", Float.class);
            Long recoveryDuration = this.getOptionalValue("shooting.recoil.recovery-duration", Long.class);

            recoil = new RecoilSpecification(recoilType, horizontalRecoilValues, verticalRecoilValues, kickbackDuration, recoveryRate, recoveryDuration);
        }

        if (yamlReader.contains("shooting.spread-pattern")) {
            String spreadPatternType = this.getRequiredValue("shooting.spread-pattern.type", String.class);
            Integer projectileAmount = this.getRequiredValue("shooting.spread-pattern.projectile-amount", Integer.class);
            Float horizontalSpread = this.getRequiredValue("shooting.spread-pattern.horizontal-spread", Float.class);
            Float verticalSpread = this.getRequiredValue("shooting.spread-pattern.vertical-spread", Float.class);

            spreadPattern = new SpreadPatternSpecification(spreadPatternType, projectileAmount, horizontalSpread, verticalSpread);
        }

        return new GunSpecification(name, description, magazineSize, maxMagazineAmount, defaultMagazineAmount, shortRangeDamage, shortRangeDistance, mediumRangeDamage, mediumRangeDistance, longRangeDamage, longRangeDistance, headshotDamageMultiplier, shotSounds, item, controls, fireMode, recoil, spreadPattern);
    }

    @NotNull
    private Material getMaterial(@NotNull String route) {
        String materialValue = this.getRequiredValue(route, String.class);

        try {
            return Material.valueOf(materialValue);
        } catch (IllegalArgumentException e) {
            String id = yamlReader.getString("id");
            throw new InvalidItemConfigurationException("Gun configuration error for %s: Material value '%s' in '%s' is invalid".formatted(id, materialValue, route));
        }
    }

    @Nullable
    private <T> T getOptionalValue(@NotNull String route, @NotNull Class<T> type) {
        return yamlReader.getAsOptional(route, type).orElse(null);
    }

    @NotNull
    private <T> T getRequiredValue(@NotNull String route, @NotNull Class<T> type) {
        return yamlReader.getAsOptional(route, type)
                .orElseThrow(() -> {
                    String id = yamlReader.getString("id");
                    return new InvalidItemConfigurationException("Gun configuration error for %s: Missing required '%s' value".formatted(id, route));
                });
    }
}
