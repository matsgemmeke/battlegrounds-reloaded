package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.audio.DefaultGameSound;
import nl.matsgemmeke.battlegrounds.game.audio.GameSound;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
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
import nl.matsgemmeke.battlegrounds.text.TextTemplate;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class FirearmFactory implements WeaponFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-gun";

    @NotNull
    private final BattlegroundsConfiguration config;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final FireModeFactory fireModeFactory;
    @NotNull
    private final NamespacedKeyCreator keyCreator;
    @NotNull
    private final RecoilProducerFactory recoilProducerFactory;
    @NotNull
    private final ReloadSystemFactory reloadSystemFactory;
    @NotNull
    private final SpreadPatternFactory spreadPatternFactory;

    @Inject
    public FirearmFactory(
            @NotNull BattlegroundsConfiguration config,
            @NotNull GameContextProvider contextProvider,
            @NotNull FireModeFactory fireModeFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull RecoilProducerFactory recoilProducerFactory,
            @NotNull ReloadSystemFactory reloadSystemFactory,
            @NotNull SpreadPatternFactory spreadPatternFactory
    ) {
        this.config = config;
        this.contextProvider = contextProvider;
        this.fireModeFactory = fireModeFactory;
        this.keyCreator = keyCreator;
        this.recoilProducerFactory = recoilProducerFactory;
        this.reloadSystemFactory = reloadSystemFactory;
        this.spreadPatternFactory = spreadPatternFactory;
    }

    @NotNull
    public Firearm make(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        Firearm firearm = this.createInstance(configuration, gameKey);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm);

        return firearm;
    }

    @NotNull
    public Firearm make(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Firearm firearm = this.createInstance(configuration, gameKey);
        firearm.setHolder(gamePlayer);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm, gamePlayer);

        return firearm;
    }

    @NotNull
    private Firearm createInstance(@NotNull ItemConfiguration configuration, @NotNull GameKey gameKey) {
        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);
        DamageProcessor damageProcessor = contextProvider.getComponent(gameKey, DamageProcessor.class);
        TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

        Section section = configuration.getRoot();

        // Descriptive attributes
        String name = section.getString("name");
        String description = section.getString("description");

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
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

        FireMode fireMode = fireModeFactory.make(firearm, section.getSection("shooting.fire-mode"));
        firearm.setFireMode(fireMode);

        // Read controls configuration
        Section controlsSection = section.getSection("controls");

        if (controlsSection != null) {
            this.addControls(firearm, gameKey, section, controlsSection);
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

        // Item template creation
        Material material;
        String materialValue = section.getString("item.material");

        try {
            material = Material.valueOf(materialValue);
        } catch (IllegalArgumentException e) {
            throw new CreateFirearmException("Unable to create firearm " + name + "; item stack material " + materialValue + " is invalid");
        }

        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        int damage = section.getInt("item.damage");
        String displayName = section.getString("item.display-name");

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.setDamage(damage);

        if (displayName != null) {
            itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));
        }

        // Set and update the item stack
        firearm.setItemTemplate(itemTemplate);
        firearm.update();

        return firearm;
    }

    private void addControls(@NotNull DefaultFirearm firearm, @NotNull GameKey gameKey, @NotNull Section section, @NotNull Section controlsSection) {
        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);

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

                ChangeScopeMagnificationFunction changeScopeMagnificationFunction = new ChangeScopeMagnificationFunction(scopeAttachment, audioEmitter);
                changeScopeMagnificationFunction.addSounds(changeMagnificationSounds);

                Action changeScopeMagnificationAction = this.getActionFromConfiguration("scope-change-magnification", changeScopeMagnificationActionValue);

                firearm.getControls().addControl(changeScopeMagnificationAction, changeScopeMagnificationFunction);
            }

            List<GameSound> useScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("use-sound"));

            UseScopeFunction useScopeFunction = new UseScopeFunction(scopeAttachment, audioEmitter);
            useScopeFunction.addSounds(useScopeSounds);

            List<GameSound> stopScopeSounds = DefaultGameSound.parseSounds(scopeSection.getString("stop-sound"));

            StopScopeFunction stopScopeFunction = new StopScopeFunction(scopeAttachment, audioEmitter);
            stopScopeFunction.addSounds(stopScopeSounds);

            Action useScopeAction = this.getActionFromConfiguration("scope-use", useScopeActionValue);
            Action stopScopeAction = this.getActionFromConfiguration("scope-stop", stopScopeActionValue);

            firearm.getControls().addControl(useScopeAction, useScopeFunction);
            firearm.getControls().addControl(stopScopeAction, stopScopeFunction);
        }

        if (reloadActionValue != null) {
            ReloadSystem reloadSystem = reloadSystemFactory.make(firearm, section.getSection("reloading"), audioEmitter);
            List<GameSound> reloadSounds = DefaultGameSound.parseSounds(section.getString("reloading.sound"));

            ReloadFunction reloadFunction = new ReloadFunction(firearm, reloadSystem);
            reloadFunction.addReloadSounds(reloadSounds);

            Action reloadAction = this.getActionFromConfiguration("reload", reloadActionValue);

            firearm.getControls().addControl(reloadAction, reloadFunction);
        }

        if (shootActionValue != null) {
            FireMode fireMode = firearm.getFireMode();
            List<GameSound> triggerSounds = DefaultGameSound.parseSounds(config.getGunTriggerSound());

            ShootFunction shootFunction = new ShootFunction(firearm, audioEmitter, fireMode);
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
