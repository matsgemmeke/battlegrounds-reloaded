package nl.matsgemmeke.battlegrounds.item.gun;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.RecoilSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.ScopeSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.SpreadPatternSpec;
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
import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.*;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.scope.DefaultScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeProperties;
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

public class FirearmFactory {

    private static final String NAMESPACED_KEY_NAME = "battlegrounds-gun";

    @NotNull
    private final BattlegroundsConfiguration config;
    @NotNull
    private final GameContextProvider contextProvider;
    @NotNull
    private final FirearmControlsFactory controlsFactory;
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
            @NotNull FirearmControlsFactory controlsFactory,
            @NotNull FireModeFactory fireModeFactory,
            @NotNull NamespacedKeyCreator keyCreator,
            @NotNull RecoilProducerFactory recoilProducerFactory,
            @NotNull ReloadSystemFactory reloadSystemFactory,
            @NotNull SpreadPatternFactory spreadPatternFactory
    ) {
        this.config = config;
        this.contextProvider = contextProvider;
        this.controlsFactory = controlsFactory;
        this.fireModeFactory = fireModeFactory;
        this.keyCreator = keyCreator;
        this.recoilProducerFactory = recoilProducerFactory;
        this.reloadSystemFactory = reloadSystemFactory;
        this.spreadPatternFactory = spreadPatternFactory;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        Firearm firearm = this.createInstance(spec, gameKey);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm);

        return firearm;
    }

    @NotNull
    public Firearm create(@NotNull GunSpec spec, @NotNull GameKey gameKey, @NotNull GamePlayer gamePlayer) {
        Firearm firearm = this.createInstance(spec, gameKey);
        firearm.setHolder(gamePlayer);

        GunRegistry gunRegistry = contextProvider.getComponent(gameKey, GunRegistry.class);
        gunRegistry.registerItem(firearm, gamePlayer);

        return firearm;
    }

    @NotNull
    private Firearm createInstance(@NotNull GunSpec spec, @NotNull GameKey gameKey) {
        AudioEmitter audioEmitter = contextProvider.getComponent(gameKey, AudioEmitter.class);
        CollisionDetector collisionDetector = contextProvider.getComponent(gameKey, CollisionDetector.class);
        DamageProcessor damageProcessor = contextProvider.getComponent(gameKey, DamageProcessor.class);
        TargetFinder targetFinder = contextProvider.getComponent(gameKey, TargetFinder.class);

        DefaultFirearm firearm = new DefaultFirearm(audioEmitter, collisionDetector, damageProcessor, targetFinder);
        firearm.setName(spec.name());
        firearm.setDescription(spec.description());
        firearm.setHeadshotDamageMultiplier(spec.headshotDamageMultiplier());

        double damageAmplifier = config.getGunDamageAmplifier();
        firearm.setDamageAmplifier(damageAmplifier);

        int magazineSize = spec.magazineSize();
        int reserveAmmo = spec.defaultMagazineAmount() * magazineSize;
        int maxAmmo = spec.maxMagazineAmount() * magazineSize;

        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(magazineSize, magazineSize, reserveAmmo, maxAmmo);
        firearm.setAmmunitionStorage(ammunitionStorage);

        RangeProfile rangeProfile = new RangeProfile(spec.rangeProfile().longRangeDamage(), spec.rangeProfile().longRangeDistance(), spec.rangeProfile().mediumRangeDamage(), spec.rangeProfile().mediumRangeDistance(), spec.rangeProfile().shortRangeDamage(), spec.rangeProfile().shortRangeDistance());
        firearm.setRangeProfile(rangeProfile);

        List<GameSound> shotSounds = DefaultGameSound.parseSounds(spec.shotSounds());
        firearm.setShotSounds(shotSounds);

        ReloadSystem reloadSystem = reloadSystemFactory.create(spec.reload(), firearm, audioEmitter);
        firearm.setReloadSystem(reloadSystem);

        ItemControls<GunHolder> controls = controlsFactory.create(spec.controls(), firearm);
        firearm.setControls(controls);

        FireMode fireMode = fireModeFactory.create(spec.fireMode(), firearm);
        firearm.setFireMode(fireMode);

        RecoilSpec recoilSpec = spec.recoil();
        ScopeSpec scopeSpec = spec.scope();
        SpreadPatternSpec spreadPatternSpec = spec.spreadPattern();

        if (recoilSpec != null) {
            RecoilProducer recoilProducer = recoilProducerFactory.create(recoilSpec);
            firearm.setRecoilProducer(recoilProducer);
        }

        if (scopeSpec != null) {
            List<Float> magnifications = scopeSpec.magnifications();
            List<GameSound> useSounds = DefaultGameSound.parseSounds(scopeSpec.useSounds());
            List<GameSound> stopSounds = DefaultGameSound.parseSounds(scopeSpec.stopSounds());
            List<GameSound> changeMagnificationSounds = DefaultGameSound.parseSounds(scopeSpec.changeMagnificationSounds());

            ScopeProperties properties = new ScopeProperties(magnifications, useSounds, stopSounds, changeMagnificationSounds);
            DefaultScopeAttachment scopeAttachment = new DefaultScopeAttachment(properties, audioEmitter);

            firearm.setScopeAttachment(scopeAttachment);
        }

        if (spreadPatternSpec != null) {
            SpreadPattern spreadPattern = spreadPatternFactory.create(spreadPatternSpec);
            firearm.setSpreadPattern(spreadPattern);
        }

        UUID uuid = UUID.randomUUID();
        NamespacedKey key = keyCreator.create(NAMESPACED_KEY_NAME);
        Material material = Material.valueOf(spec.item().material());
        String displayName = spec.item().displayName();
        int damage = spec.item().damage();

        ItemTemplate itemTemplate = new ItemTemplate(uuid, key, material);
        itemTemplate.setDamage(damage);
        itemTemplate.setDisplayNameTemplate(new TextTemplate(displayName));

        // Set and update the item stack
        firearm.setItemTemplate(itemTemplate);
        firearm.update();

        return firearm;
    }
}
