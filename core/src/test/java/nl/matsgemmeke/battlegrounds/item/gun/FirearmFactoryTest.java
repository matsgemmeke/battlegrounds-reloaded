package nl.matsgemmeke.battlegrounds.item.gun;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContextProvider;
import nl.matsgemmeke.battlegrounds.game.GameKey;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.damage.DamageProcessor;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.game.component.TargetFinder;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class FirearmFactoryTest {

    private BattlegroundsConfiguration config;
    private GameContextProvider contextProvider;
    private GameKey gameKey;
    private GunRegistry gunRegistry;
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
        config = mock(BattlegroundsConfiguration.class);
        gameKey = GameKey.ofTrainingMode();
        gunRegistry = mock(GunRegistry.class);
        fireModeFactory = mock(FireModeFactory.class);
        itemFactory = mock(ItemFactory.class);
        recoilProducerFactory = mock(RecoilProducerFactory.class);
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        spreadPatternFactory = mock(SpreadPatternFactory.class);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
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
        when(rootSection.getString("description")).thenReturn("test");
        when(rootSection.getString("name")).thenReturn("test");
        when(rootSection.getString("shooting.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        itemConfiguration = mock(ItemConfiguration.class);
        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");
        when(itemConfiguration.getRoot()).thenReturn(rootSection);

        Section fireModeSection = mock(Section.class);
        when(rootSection.getSection("shooting.fire-mode")).thenReturn(fireModeSection);

        FireMode fireMode = mock(FireMode.class);
        when(fireModeFactory.make(any(Shootable.class), eq(fireModeSection))).thenReturn(fireMode);

        bukkit = mockStatic(Bukkit.class);
        bukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
    }

    @AfterEach
    public void tearDown() {
        bukkit.close();
    }

    @Test
    public void createSimpleFirearm() {
        int damage = 1;
        int defaultSupply = 3;
        int magazineSize = 10;
        int maxMagazineAmount = 10;
        int reserveAmmo = defaultSupply * magazineSize;
        int maxAmmo = maxMagazineAmount * magazineSize;

        PersistentDataContainer dataContainer = mock(PersistentDataContainer.class);

        Damageable itemMeta = mock(Damageable.class);
        when(itemMeta.getPersistentDataContainer()).thenReturn(dataContainer);

        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");
        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);

        when(rootSection.getInt("ammo.default-supply")).thenReturn(defaultSupply);
        when(rootSection.getInt("ammo.magazine-size")).thenReturn(magazineSize);
        when(rootSection.getInt("ammo.max-magazine-amount")).thenReturn(maxMagazineAmount);
        when(rootSection.getInt("item.damage")).thenReturn(damage);
        when(rootSection.getString("item.display-name")).thenReturn("%name%");
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals("test", firearm.getName());
        assertEquals(Material.IRON_HOE, firearm.getItemStack().getType());
        assertEquals(magazineSize, firearm.getMagazineAmmo());
        assertEquals(magazineSize, firearm.getMagazineSize());
        assertEquals(maxAmmo, firearm.getMaxAmmo());
        assertEquals(reserveAmmo, firearm.getReserveAmmo());

        verify(gunRegistry).registerItem(firearm);
        verify(itemMeta).setDamage(damage);
        verify(itemMeta).setDisplayName("test");
    }

    @Test
    public void shouldThrowExceptionWhenCreatingFirearmWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);

        assertThrows(CreateFirearmException.class, () -> firearmFactory.make(itemConfiguration, gameKey));
    }

    @Test
    public void createFirearmWithShootControlsConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("shoot")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(fireModeFactory).make(eq(firearm), any());
        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithSpreadPatternFromConfiguration() {
        Section patternSection = mock(Section.class);

        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("shooting.pattern")).thenReturn(patternSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(gunRegistry).registerItem(firearm);
        verify(spreadPatternFactory).make(patternSection);
    }

    @Test
    public void createFirearmWithRecoilProducerFromConfiguration() {
        Section recoilSection = mock(Section.class);

        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("shooting.recoil")).thenReturn(recoilSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(gunRegistry).registerItem(firearm);
        verify(recoilProducerFactory).make(recoilSection);
    }

    @Test
    public void createFirearmWithReloadControlsConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("reload")).thenReturn("LEFT_CLICK");

        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        when(reloadSystemFactory.make(any(), any(), any())).thenReturn(reloadSystem);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);

        verify(gunRegistry).registerItem(firearm);
        verify(reloadSystemFactory).make(eq(firearm), any(), any());
    }

    @Test
    public void createFirearmWithScopeControlsConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");
        when(scopeSection.getString("use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals("test", firearm.getName());

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithScopeControlsAndMagnificationConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-change-magnification")).thenReturn("SWAP_FROM");
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getString("change-magnification-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f, -0.2f));
        when(scopeSection.getString("stop-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");
        when(scopeSection.getString("use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals("test", firearm.getName());

        verify(gunRegistry).registerItem(firearm);
    }

    @Test
    public void shouldThrowErrorWhenScopeUseActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("fail");
        when(controlsSection.getString("scope-stop")).thenReturn("LEFT_CLICK");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getString("use-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);

        assertThrows(CreateFirearmException.class, () -> firearmFactory.make(itemConfiguration, gameKey));
    }

    @Test
    public void shouldThrowErrorWhenScopeStopActionConfigurationValueIsInvalid() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("scope-use")).thenReturn("RIGHT_CLICK");
        when(controlsSection.getString("scope-stop")).thenReturn("fail");

        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloatList("magnifications")).thenReturn(List.of(-0.1f));
        when(scopeSection.getString("stop-sound")).thenReturn("AMBIENT_CAVE-1-1-1");
        when(scopeSection.getString("use-sound")).thenReturn("AMBIENT_CAVE-1-1-1");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);

        assertThrows(CreateFirearmException.class, () -> firearmFactory.make(itemConfiguration, gameKey));
    }

    @Test
    public void createFirearmAndAssignPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, contextProvider, fireModeFactory, keyCreator, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, gameKey, gamePlayer);

        assertInstanceOf(DefaultFirearm.class, firearm);
        assertEquals(gamePlayer, firearm.getHolder());

        verify(gunRegistry).registerItem(firearm, gamePlayer);
    }
}
