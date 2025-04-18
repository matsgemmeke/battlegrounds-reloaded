package nl.matsgemmeke.battlegrounds.item.gun;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
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
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducer;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.reload.Reloadable;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.Shootable;
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
    private ItemConfiguration itemConfiguration;
    private ItemFactory itemFactory;
    private MockedStatic<Bukkit> bukkit;
    private NamespacedKeyCreator keyCreator;
    private RecoilProducerFactory recoilProducerFactory;
    private ReloadSystemFactory reloadSystemFactory;
    private Section rootSection;
    private SpreadPatternFactory spreadPatternFactory;

    @BeforeEach
    public void setUp() {
        audioEmitter = mock(AudioEmitter.class);
        config = mock(BattlegroundsConfiguration.class);
        gameKey = GameKey.ofTrainingMode();
        gunRegistry = mock(GunRegistry.class);
        controlsFactory = mock(FirearmControlsFactory.class);
        fireModeFactory = mock(FireModeFactory.class);
        itemFactory = mock(ItemFactory.class);
        recoilProducerFactory = mock(RecoilProducerFactory.class);
        reloadSystemFactory = mock(ReloadSystemFactory.class);
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

        rootSection = mock(Section.class);

        itemConfiguration = mock(ItemConfiguration.class);
        when(itemConfiguration.getRoot()).thenReturn(rootSection);

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

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", ITEM_DAMAGE);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        GunSpec gunSpec = new GunSpec("Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, 10.0, 35.0, 20.0, 25.0, 30.0, 15.0, 1.5, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec), any(Firearm.class))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, itemConfiguration, gameKey);

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
        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        SpreadPatternSpec spreadPatternSpec = new SpreadPatternSpec("BUCKSHOT", 1, 0.5f, 0.5f);
        GunSpec gunSpec = new GunSpec("Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, 10.0, 35.0, 20.0, 25.0, 30.0, 15.0, 1.5, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, null, spreadPatternSpec);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec), any(Shootable.class))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, itemConfiguration, gameKey);

        assertThat(firearm).isInstanceOf(DefaultFirearm.class);

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithRecoilFromConfiguration() {
        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        RecoilSpec recoilSpec = new RecoilSpec("RANDOM_SPREAD", List.of(), List.of(), null, null, null);
        GunSpec gunSpec = new GunSpec("Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, 10.0, 35.0, 20.0, 25.0, 30.0, 15.0, 1.5, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, recoilSpec, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec), any(Firearm.class))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        RecoilProducer recoilProducer = mock(RecoilProducer.class);
        when(recoilProducerFactory.create(recoilSpec)).thenReturn(recoilProducer);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createMakesFirearmInstanceWithScopeAttachmentIfConfigurationIsPresent() {
        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f, -0.2f));

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        GunSpec gunSpec = new GunSpec("Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, 10.0, 35.0, 20.0, 25.0, 30.0, 15.0, 1.5, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec), any(Firearm.class))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertNotNull(firearm.getScopeAttachment());

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createMakesFirearmAndAssignsPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ReloadSpec reloadSpec = new ReloadSpec("MAGAZINE", null, 20L);
        ItemStackSpec itemSpec = new ItemStackSpec("IRON_HOE", "Test Gun", 1);
        ControlsSpec controlsSpec = new ControlsSpec("LEFT_CLICK", "RIGHT_CLICK", null, null, null);
        FireModeSpec fireModeSpec = new FireModeSpec("SEMI_AUTOMATIC", null, null, 5L);
        GunSpec gunSpec = new GunSpec("Test Gun", "Test description", MAGAZINE_SIZE, MAX_MAGAZINE_AMOUNT, DEFAULT_MAGAZINE_AMOUNT, 10.0, 35.0, 20.0, 25.0, 30.0, 15.0, 1.5, null, reloadSpec, itemSpec, controlsSpec, fireModeSpec, null, null);

        ItemControls<GunHolder> controls = new ItemControls<>();
        when(controlsFactory.create(eq(controlsSpec), any(Firearm.class))).thenReturn(controls);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.create(eq(fireModeSpec), any(Shootable.class))).thenReturn(fireMode);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystemFactory.create(eq(reloadSpec), any(Reloadable.class), eq(audioEmitter))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, controlsFactory, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.create(gunSpec, itemConfiguration, gameKey, gamePlayer);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals(gamePlayer, firearm.getHolder());

        verify(gunRegistry).registerItem(firearm, gamePlayer);
    }
}
