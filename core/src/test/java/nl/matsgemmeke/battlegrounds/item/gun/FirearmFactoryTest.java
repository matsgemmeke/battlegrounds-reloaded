package nl.matsgemmeke.battlegrounds.item.gun;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.game.component.AudioEmitter;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.ItemRegistry;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.Damageable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unchecked")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class FirearmFactoryTest {

    private BattlegroundsConfiguration config;
    private GameContext context;
    private FireModeFactory fireModeFactory;
    private ItemConfiguration itemConfiguration;
    private ItemFactory itemFactory;
    private RecoilProducerFactory recoilProducerFactory;
    private ReloadSystemFactory reloadSystemFactory;
    private Section rootSection;
    private SpreadPatternFactory spreadPatternFactory;

    @Before
    public void setUp() {
        config = mock(BattlegroundsConfiguration.class);
        fireModeFactory = mock(FireModeFactory.class);
        itemFactory = mock(ItemFactory.class);
        recoilProducerFactory = mock(RecoilProducerFactory.class);
        reloadSystemFactory = mock(ReloadSystemFactory.class);
        spreadPatternFactory = mock(SpreadPatternFactory.class);

        AudioEmitter audioEmitter = mock(AudioEmitter.class);
        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        context = mock(GameContext.class);
        when(context.getAudioEmitter()).thenReturn(audioEmitter);
        when(context.getCollisionDetector()).thenReturn(collisionDetector);

        rootSection = mock(Section.class);
        when(rootSection.getString("description")).thenReturn("test");
        when(rootSection.getString("display-name")).thenReturn("test");
        when(rootSection.getString("shooting.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        this.itemConfiguration = mock(ItemConfiguration.class);
        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");
        when(itemConfiguration.getRoot()).thenReturn(rootSection);

        PowerMockito.mockStatic(Bukkit.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void createSimpleFirearm() {
        int damage = 1;
        int defaultSupply = 3;
        int magazineSize = 10;
        int maxMagazineAmount = 10;
        int reserveAmmo = defaultSupply * magazineSize;
        int maxAmmo = maxMagazineAmount * magazineSize;

        Damageable itemMeta = mock(Damageable.class);

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");
        when(itemFactory.getItemMeta(Material.IRON_HOE)).thenReturn(itemMeta);

        when(rootSection.getInt("ammo.default-supply")).thenReturn(defaultSupply);
        when(rootSection.getInt("ammo.magazine-size")).thenReturn(magazineSize);
        when(rootSection.getInt("ammo.max-magazine-amount")).thenReturn(maxMagazineAmount);
        when(rootSection.getInt("item.damage")).thenReturn(damage);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertTrue(firearm instanceof DefaultFirearm);
        assertEquals("test", firearm.getName());
        assertEquals(Material.IRON_HOE, firearm.getItemStack().getType());
        assertEquals(magazineSize, firearm.getMagazineAmmo());
        assertEquals(magazineSize, firearm.getMagazineSize());
        assertEquals(maxAmmo, firearm.getMaxAmmo());
        assertEquals(reserveAmmo, firearm.getReserveAmmo());

        verify(itemMeta).setDamage(damage);
        verify(registry).registerItem(firearm);
    }

    @Test(expected = CreateFirearmException.class)
    public void shouldThrowExceptionWhenCreatingFirearmWithInvalidMaterial() {
        when(rootSection.getString("item.material")).thenReturn("fail");

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        firearmFactory.make(itemConfiguration, context);
    }

    @Test
    public void createFirearmWithShootControlsConfiguration() {
        FireMode fireMode = mock(FireMode.class);

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("shoot")).thenReturn("RIGHT_CLICK");

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(fireModeFactory.make(any(), any())).thenReturn(fireMode);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);

        verify(fireModeFactory).make(eq(firearm), any());
        verify(registry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithSpreadPatternFromConfiguration() {
        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        Section patternSection = mock(Section.class);

        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("shooting.pattern")).thenReturn(patternSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);

        verify(registry).registerItem(firearm);
        verify(spreadPatternFactory).make(patternSection);
    }

    @Test
    public void createFirearmWithRecoilProducerFromConfiguration() {
        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        Section recoilSection = mock(Section.class);

        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("shooting.recoil")).thenReturn(recoilSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);

        verify(recoilProducerFactory).make(recoilSection);
        verify(registry).registerItem(firearm);
    }

    @Test
    public void createFirearmWithReloadControlsConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("reload")).thenReturn("LEFT_CLICK");

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        when(reloadSystemFactory.make(any(), any(), any())).thenReturn(reloadSystem);
        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);

        verify(reloadSystemFactory).make(eq(firearm), any(), any());
        verify(registry).registerItem(firearm);
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

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        when(rootSection.getSection("controls")).thenReturn(controlsSection);
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");
        when(rootSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());

        verify(registry).registerItem(firearm);
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

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());

        verify(registry).registerItem(firearm);
    }

    @Test(expected = CreateFirearmException.class)
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

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        firearmFactory.make(itemConfiguration, context);
    }

    @Test(expected = CreateFirearmException.class)
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

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        firearmFactory.make(itemConfiguration, context);
    }

    @Test
    public void createFirearmAndAssignPlayer() {
        GamePlayer gamePlayer = mock(GamePlayer.class);

        ItemRegistry<Gun, GunHolder> registry = (ItemRegistry<Gun, GunHolder>) mock(ItemRegistry.class);
        when(context.getGunRegistry()).thenReturn(registry);

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(rootSection.getString("item.material")).thenReturn("IRON_HOE");

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, context, gamePlayer);

        assertEquals(gamePlayer, firearm.getHolder());

        verify(registry).registerItem(firearm, gamePlayer);
    }
}
