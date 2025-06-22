package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.item.particle.ParticleEffectSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.ControlsSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.gun.GunSpec;
import nl.matsgemmeke.battlegrounds.configuration.spec.item.*;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.gun.controls.FirearmControlsFactory;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandlerFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.firemode.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import nl.matsgemmeke.battlegrounds.util.NamespacedKeyCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FirearmFactoryTest {

    private static final int DEFAULT_MAGAZINE_AMOUNT = 3;
    private static final int ITEM_DAMAGE = 1;
    private static final int MAGAZINE_SIZE = 10;
    private static final int MAX_MAGAZINE_AMOUNT = 5;

    private AudioEmitter audioEmitter;
    private BattlegroundsConfiguration config;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunRegistry gunRegistry;
    private FirearmControlsFactory controlsFactory;
    private FireModeFactory fireModeFactory;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private RecoilFactory recoilFactory;
    private ReloadSystemFactory reloadSystemFactory;
    private ShootHandlerFactory shootHandlerFactory;
    private SpreadPatternFactory spreadPatternFactory;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        config = mock(BattlegroundsConfiguration.class);
        gameKey = GameKey.ofOpenMode();
        gunRegistry = mock(GunRegistry.class);
        controlsFactory = mock(FirearmControlsFactory.class);
        fireModeFactory = mock(FireModeFactory.class);
        itemFactory = mock(ItemFactory.class);
        recoilFactory = mock(RecoilFactory.class);
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        shootHandlerFactory = mock(ShootHandlerFactory.class);
        spreadPatternFactory = mock(SpreadPatternFactory.class);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);
        DamageProcessor damageProcessor = mock(DamageProcessor.class);
        TargetFinder targetFinder = mock(TargetFinder.class);

        contextProvider = mock(GameContextProvider.class);
        when(contextProvider.getComponent(gameKey, AudioEmitter.class)).thenReturn(audioEmitter);
        when(contextProvider.getComponent(gameKey, CollisionDetector.class)).thenReturn(collisionDetector);
        when(contextProvider.getComponent(gameKey, DamageProcessor.class)).thenReturn(damageProcessor);
        when(contextProvider.getComponent(gameKey, GunRegistry.class)).thenReturn(gunRegistry);
        when(contextProvider.getComponent(gameKey, TargetFinder.class)).thenReturn(targetFinder);

        Plugin plugin = mock(Plugin.class);
        when(plugin.getName()).thenReturn("Battlegrounds");

        NamespacedKey key = new NamespacedKey(plugin, "battlegrounds-gun");

        keyCreator = mock(NamespacedKeyCreator.class);
        when(keyCreator.create("battlegrounds-gun")).thenReturn(key);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createSimpleFirearm() {
        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", ITEM_DAMAGE);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        GunSpec gunSpec = new GunSpec("TEST_EQUIPMENT", "Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, rangeProfileSpec, 1.5, shootingSpec, reloadSpec, itemSpec, controlsSpec, null, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, keyCreator, recoilFactory, reloadSystemFactory, shootHandlerFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, gameKey);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);
        assertThat(firearm.getName()).isEqualTo("Test Gun");
        assertThat(firearm.getItemStack()).isNotNull();
        assertThat(firearm.getItemStack().getType()).isEqualTo(Material.IRON_HOE);
        assertThat(firearm.getAmmunitionStorage().getMagazineAmmo()).isEqualTo(MAGAZINE_SIZE);
        assertThat(firearm.getAmmunitionStorage().getMagazineSize()).isEqualTo(MAGAZINE_SIZE);
        assertThat(firearm.getAmmunitionStorage().getMaxAmmo()).isEqualTo(50);
        assertThat(firearm.getAmmunitionStorage().getReserveAmmo()).isEqualTo(30);

        verify(gunRegistry).registerItem(firearm);
        verify(itemMeta).setDamage(ITEM_DAMAGE);
        verify(itemMeta).setDisplayName("Test Gun");
    }

    @Test
    public void createFirearmWithSpreadPatternFromConfiguration() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("BUCKSHOT", 1, 0.5f, 0.5f);
        GunSpec gunSpec = new GunSpec("TEST_EQUIPMENT", "Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, rangeProfileSpec, 1.5, shootingSpec, reloadSpec, itemSpec, controlsSpec, null, null, spreadPatternSpec);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, keyCreator, recoilFactory, reloadSystemFactory, shootHandlerFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, gameKey);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithRecoilFromConfiguration() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        RecoilSpec recoilSpec = new RecoilSpec("RANDOM_SPREAD", List.of(), List.of(), null, null, null);
        GunSpec gunSpec = new GunSpec("TEST_EQUIPMENT", "Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, rangeProfileSpec, 1.5, shootingSpec, reloadSpec, itemSpec, controlsSpec, recoilSpec, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        Recoil recoil = mock(Recoil.class);
        when(recoilFactory.create(recoilSpec)).thenReturn(recoil);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, keyCreator, recoilFactory, reloadSystemFactory, shootHandlerFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createMakesFirearmInstanceWithScopeAttachmentIfConfigurationIsPresent() {
        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        ScopeSpec scopeSpec = new ScopeSpec(List.of(-0.1f, -0.2f), null, null, null);
        GunSpec gunSpec = new GunSpec("TEST_EQUIPMENT", "Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, rangeProfileSpec, 1.5, shootingSpec, reloadSpec, itemSpec, controlsSpec, null, scopeSpec, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, keyCreator, recoilFactory, reloadSystemFactory, shootHandlerFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertNotNull(firearm.getScopeAttachment());

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createMakesFirearmAndAssignsPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        RangeProfileSpec rangeProfileSpec = new RangeProfileSpec(10.0, 35.0, 20.0, 25.0, 30.0, 15.0);

        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        ParticleEffectSpec trajectoryParticleEffectSpec = new ParticleEffectSpec("FLAME", 1, 0.0, 0.0, 0.0, 0.0, null, null);
        ProjectileSpec projectileSpec = new ProjectileSpec(trajectoryParticleEffectSpec);
        ShootingSpec shootingSpec = new ShootingSpec(fireModeSpec, projectileSpec, null);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        GunSpec gunSpec = new GunSpec("TEST_EQUIPMENT", "Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, rangeProfileSpec, 1.5, shootingSpec, reloadSpec, itemSpec, controlsSpec, null, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, keyCreator, recoilFactory, reloadSystemFactory, shootHandlerFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, gameKey, gamePlayer);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals(gamePlayer, firearm.getHolder());

        verify(gunRegistry).registerItem(firearm, gamePlayer);
    }
}
