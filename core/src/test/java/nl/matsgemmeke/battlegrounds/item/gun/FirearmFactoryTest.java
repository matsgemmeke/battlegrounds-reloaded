package nl.matsgemmeke.battlegrounds.item.gun;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import nl.matsgemmeke.battlegrounds.configuration.BattlegroundsConfiguration;
import nl.matsgemmeke.battlegrounds.configuration.ItemConfiguration;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.entity.GunHolder;
import nl.matsgemmeke.battlegrounds.game.Game;
import nl.matsgemmeke.battlegrounds.game.component.CollisionDetector;
import nl.matsgemmeke.battlegrounds.game.component.GameContext;
import nl.matsgemmeke.battlegrounds.item.ItemRegister;
import nl.matsgemmeke.battlegrounds.item.recoil.RecoilProducerFactory;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystemFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.FireMode;
import nl.matsgemmeke.battlegrounds.item.shoot.FireModeFactory;
import nl.matsgemmeke.battlegrounds.item.shoot.spread.SpreadPatternFactory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
    private RecoilProducerFactory recoilProducerFactory;
    private ReloadSystemFactory reloadSystemFactory;
    private Section mainSection;
    private SpreadPatternFactory spreadPatternFactory;

    @Before
    public void setUp() {
        this.config = mock(BattlegroundsConfiguration.class);
        this.fireModeFactory = mock(FireModeFactory.class);
        this.recoilProducerFactory = mock(RecoilProducerFactory.class);
        this.reloadSystemFactory = mock(ReloadSystemFactory.class);
        this.spreadPatternFactory = mock(SpreadPatternFactory.class);

        CollisionDetector collisionDetector = mock(CollisionDetector.class);

        context = mock(GameContext.class);
        when(context.getCollisionDetector()).thenReturn(collisionDetector);

        this.mainSection = mock(Section.class);
        when(mainSection.getString("description")).thenReturn("test");
        when(mainSection.getString("display-name")).thenReturn("test");
        when(mainSection.getString("item.material")).thenReturn("IRON_HOE");
        when(mainSection.getString("shooting.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        this.itemConfiguration = mock(ItemConfiguration.class);
        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");
        when(itemConfiguration.getRoot()).thenReturn(mainSection);

        PowerMockito.mockStatic(Bukkit.class);
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void createFirearmWithoutScopeFromConfiguration() {
        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        when(itemConfiguration.getItemId()).thenReturn("TEST_GUN");

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());

        verify(register).addUnassignedItem(firearm);
    }

    @Test
    public void createFirearmWithShootControlsConfiguration() {
        FireMode fireMode = mock(FireMode.class);
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);
        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("shoot")).thenReturn("RIGHT_CLICK");

        when(mainSection.getSection("controls")).thenReturn(controlsSection);

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(fireModeFactory.make(any(), any())).thenReturn(fireMode);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);

        verify(fireModeFactory).make(eq(firearm), any());
    }

    @Test
    public void createFirearmWithSpreadPatternFromConfiguration() {
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);
        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        Section patternSection = mock(Section.class);
        when(mainSection.getSection("shooting.pattern")).thenReturn(patternSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);

        verify(spreadPatternFactory).make(patternSection);
    }

    @Test
    public void createFirearmWithRecoilProducerFromConfiguration() {
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);
        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        Section recoilSection = mock(Section.class);
        when(mainSection.getSection("shooting.recoil")).thenReturn(recoilSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);

        verify(recoilProducerFactory).make(recoilSection);
        verify(register).addUnassignedItem(firearm);
    }

    @Test
    public void createFirearmWithReloadControlsConfiguration() {
        Section controlsSection = mock(Section.class);
        when(controlsSection.getString("reload")).thenReturn("LEFT_CLICK");

        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        when(mainSection.getSection("controls")).thenReturn(controlsSection);
        when(reloadSystemFactory.make(any(), any(), eq(old))).thenReturn(reloadSystem);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);

        verify(reloadSystemFactory).make(eq(firearm), any(), eq(old));
        verify(register).addUnassignedItem(firearm);
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

        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        when(mainSection.getSection("controls")).thenReturn(controlsSection);
        when(mainSection.getSection("scope")).thenReturn(scopeSection);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());

        verify(register).addUnassignedItem(firearm);
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

        when(mainSection.getSection("controls")).thenReturn(controlsSection);
        when(mainSection.getSection("scope")).thenReturn(scopeSection);

        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());

        verify(register).addUnassignedItem(firearm);
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

        when(mainSection.getSection("controls")).thenReturn(controlsSection);
        when(mainSection.getSection("scope")).thenReturn(scopeSection);

        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        firearmFactory.make(itemConfiguration, game, old);
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

        when(mainSection.getSection("controls")).thenReturn(controlsSection);
        when(mainSection.getSection("scope")).thenReturn(scopeSection);

        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        firearmFactory.make(itemConfiguration, game, old);
    }

    @Test
    public void createFirearmAndAssignPlayer() {
        nl.matsgemmeke.battlegrounds.game.GameContext old = mock(nl.matsgemmeke.battlegrounds.game.GameContext.class);
        GamePlayer gamePlayer = mock(GamePlayer.class);
        ItemRegister<Gun, GunHolder> register = (ItemRegister<Gun, GunHolder>) mock(ItemRegister.class);

        Game game = mock(Game.class);
        when(game.getContext()).thenReturn(context);
        when(game.getGunRegister()).thenReturn(register);

        when(config.getGunTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(mainSection.getInt("ammo.default-supply")).thenReturn(3);
        when(mainSection.getInt("ammo.magazine-size")).thenReturn(10);
        when(mainSection.getInt("ammo.max-magazine-amount")).thenReturn(10);

        FirearmFactory firearmFactory = new FirearmFactory(config, fireModeFactory, recoilProducerFactory, reloadSystemFactory, spreadPatternFactory);
        Firearm firearm = firearmFactory.make(itemConfiguration, game, old, gamePlayer);

        assertEquals(gamePlayer, firearm.getHolder());
        assertEquals(10, firearm.getMagazineAmmo());
        assertEquals(10, firearm.getMagazineSize());
        assertEquals(100, firearm.getMaxAmmo());
        assertEquals(30, firearm.getReserveAmmo());

        verify(register).addAssignedItem(firearm, gamePlayer);
    }
}
