package nl.matsgemmeke.battlegrounds.item.gun;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.item.WeaponFactory;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.gun.controls.*;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPattern;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FirearmFactory implements WeaponFactory {

    @NotNull
    private BattlegroundsConfiguration config;
    @NotNull
    private FireModeFactory fireModeFactory;
    @NotNull
    private RecoilProducerFactory recoilProducerFactory;
    @NotNull
    private ReloadSystemFactory reloadSystemFactory;
    @NotNull
    private SpreadPatternFactory spreadPatternFactory;

    public FirearmFactory(
            @NotNull BattlegroundsConfiguration config,
            @NotNull FireModeFactory fireModeFactory,
            @NotNull RecoilProducerFactory recoilProducerFactory,
            @NotNull ReloadSystemFactory reloadSystemFactory,
            @NotNull SpreadPatternFactory spreadPatternFactory
    ) {
        this.config = config;
        this.fireModeFactory = fireModeFactory;
        this.recoilProducerFactory = recoilProducerFactory;
        this.reloadSystemFactory = reloadSystemFactory;
        this.spreadPatternFactory = spreadPatternFactory;
    }

    @NotNull
    public Firearm make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context) {
        Firearm firearm = this.createInstance(configuration, context);

        game.getGunRegister().addUnassignedItem(firearm);

        return firearm;
    }

    @NotNull
    public Firearm make(@NotNull ItemConfiguration configuration, @NotNull Game game, @NotNull GameContext context, @NotNull GamePlayer gamePlayer) {
        Firearm firearm = this.createInstance(configuration, context);
        firearm.setHolder(gamePlayer);

        game.getGunRegister().addAssignedItem(firearm, gamePlayer);

        return firearm;
    }

    @NotNull
    private Firearm createInstance(@NotNull ItemConfiguration configuration, @NotNull GameContext context) {
        Section section = configuration.getRoot();

        // Descriptive attributes
        String name = section.getString("display-name");
        String description = section.getString("description");

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setDescription(description);
        firearm.setName(name);

        // Other variables
        double accuracy = section.getDouble("shooting.accuracy");
        firearm.setAccuracy(accuracy);

        double damageAmplifier = config.getGunDamageAmplifier();
        firearm.setDamageAmplifier(damageAmplifier);

        double headshotDamageMultiplier = section.getDouble("shooting.headshot-damage-multiplier");
        firearm.setHeadshotDamageMultiplier(headshotDamageMultiplier);

        int magazineSize = section.getInt("ammo.magazine-size");
        int maxMagazineAmount = section.getInt("ammo.max-magazine-amount");
        int reserveAmmo = section.getInt("ammo.default-supply") * magazineSize;

        firearm.setMagazineAmmo(magazineSize);
        firearm.setMagazineSize(magazineSize);
        firearm.setMaxAmmo(maxMagazineAmount * magazineSize);
        firearm.setReserveAmmo(reserveAmmo);

        double shortDamage = section.getDouble("shooting.range.short-range.damage");
        firearm.setShortDamage(shortDamage);

        double shortRange = section.getDouble("shooting.range.short-range.distance");
        firearm.setShortRange(shortRange);

        double mediumDamage = section.getDouble("shooting.range.medium-range.damage");
        firearm.setMediumDamage(mediumDamage);

        double mediumRange = section.getDouble("shooting.range.medium-range.distance");
        firearm.setMediumRange(mediumRange);

        double longDamage = section.getDouble("shooting.range.long-range.damage");
        firearm.setLongDamage(longDamage);

        double longRange = section.getDouble("shooting.range.long-range.distance");
        firearm.setLongRange(longRange);

        List<GameSound> shotSounds = DefaultGameSound.parseSounds(section.getString("shooting.shot-sound"));
        firearm.setShotSounds(shotSounds);

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            this.addControls(firearm, context, section, controlsSection);
        }

        // Handle the pattern section if it's there
        Section patternSection = section.getSection("shooting.pattern");

        if (patternSection != null) {
            SpreadPattern spreadPattern = spreadPatternFactory.make(patternSection);
            firearm.setSpreadPattern(spreadPattern);
        }

        // Handle the recoil section if it's there
        Section recoilSection = section.getSection("shooting.recoil");

        if (recoilSection != null) {
            RecoilProducer recoilProducer = recoilProducerFactory.make(recoilSection);
            firearm.setRecoilProducer(recoilProducer);
        }

        // ItemStack creation
        Material material = Material.getMaterial(section.getString("item.material"));
        short durability = section.getShort("item.durability");

        ItemStack itemStack = new ItemStack(material);
        itemStack.setDurability(durability);

        // Set and update the item stack
        firearm.setItemStack(itemStack);
        firearm.update();

        return firearm;
    }

    private void addControls(@NotNull DefaultFirearm firearm, @NotNull GameContext context, @NotNull Section section, @NotNull Section controlsSection) {
        String reloadActionValue = controlsSection.getString("reload");
        String changeScopeMagnificationActionValue = controlsSection.getString("scope-change-magnification");
        String stopScopeActionValue = controlsSection.getString("scope-stop");
        String useScopeActionValue = controlsSection.getString("scope-use");
        String shootActionValue = controlsSection.getString("shoot");

        if (useScopeActionValue != null && stopScopeActionValue != null) {
            // Assume the gun also has a configuration for the scope
            Section scopeSection = section.getSection("scope");

            List<Float> magnificationSettings = scopeSection.getFloatList("magnifications");

            DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(magnificationSettings);

            if (changeScopeMagnificationActionValue != null) {
                List<GameSound> changeMagnificationSounds = DefaultGameSound.parseSounds(scopeSection.getString("change-magnification-sound"));

                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(scopeAttachment, context);
                changeScopeMagnificationFunction.addSounds(changeMagnificationSounds);

                Action changeScopeMagnificationAction = this.getActionFromConfiguration("scope-change-magnification", changeScopeMagnificationActionValue);

                firearm.getControls().addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            List<GameSound> useScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("use-sound"));

            UseScopeFunction useScopeFunction = new UseScopeFunction(scopeAttachment, context);
            useScopeFunction.addSounds(useScopeSounds);

            List<GameSound> stopScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("stop-sound"));

            StopScopeFunction stopScopeFunction = new StopScopeFunction(scopeAttachment, context);
            stopScopeFunction.addSounds(stopScopeSounds);

            Action useScopeAction = this.getActionFromConfiguration("scope-use", useScopeActionValue);
            Action stopScopeAction = this.getActionFromConfiguration("scope-stop", stopScopeActionValue);

            firearm.getControls().addControl(useScopeAction, useScopeFunction);
            firearm.getControls().addControl(stopScopeAction, stopScopeFunction);
        }

        if (reloadActionValue != null) {
            ReloadSystem reloadSystem = reloadSystemFactory.make(firearm, section.getSection("reloading"), context);
            List<GameSound> reloadSounds = DefaultGameSound.parseSounds(section.getString("reloading.sound"));

            ReloadFunction reloadFunction = new ReloadFunction(firearm, reloadSystem);
            reloadFunction.addReloadSounds(reloadSounds);

            Action reloadAction = this.getActionFromConfiguration("reload", reloadActionValue);

            firearm.getControls().addControl(reloadAction, reloadFunction);
        }

        if (shootActionValue != null) {
            FireMode fireMode = fireModeFactory.make(firearm, section.getSection("shooting.fire-mode"));
            List<GameSound> triggerSounds = DefaultGameSound.parseSounds(config.getGunTriggerSound());

            ShootFunction shootFunction = new ShootFunction(firearm, context, fireMode);
            shootFunction.setTriggerSounds(triggerSounds);

            Action shootAction = this.getActionFromConfiguration("shoot", shootActionValue);

            firearm.getControls().addControl(shootAction, shootFunction);
        }
    }

    @NotNull
    private Action getActionFromConfiguration(@NotNull String functionName, @NotNull String value) {
        try {
            return Action.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new CreateFirearmException("Error while getting controls for " + functionName + ": \""
                    + value + "\" is not a valid action type!");
        }
    }
}
