package com.github.matsgemmeke.battlegounds.item.factory;

import com.github.matsgemmeke.battlegrounds.InternalsProvider;
import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.Firearm;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.item.factory.FireModeFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.FirearmFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.InvalidBattleItemFormatException;
import com.github.matsgemmeke.battlegrounds.item.factory.ReloadSystemFactory;
import com.github.matsgemmeke.battlegrounds.item.recoil.RecoilSystemFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bukkit.class)
public class FirearmFactoryTest {

    private BattlegroundsConfig config;
    private BattleItemConfiguration itemConfiguration;
    private FireModeFactory fireModeFactory;
    private GameContext context;
    private InternalsProvider internals;
    private RecoilSystemFactory recoilSystemFactory;
    private ReloadSystemFactory reloadSystemFactory;

    @Before
    public void setUp() {
        this.config = mock(BattlegroundsConfig.class);
        this.itemConfiguration = mock(BattleItemConfiguration.class);
        this.fireModeFactory = mock(FireModeFactory.class);
        this.context = mock(GameContext.class);
        this.internals = mock(InternalsProvider.class);
        this.recoilSystemFactory = mock(RecoilSystemFactory.class);
        this.reloadSystemFactory = mock(ReloadSystemFactory.class);

        PowerMockito.mockStatic(Bukkit.class);
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void createFirearmWithoutScopeFromConfiguration() {
        Section section = mock(Section.class);
        when(section.getString("description")).thenReturn("test");
        when(section.getString("display-name")).thenReturn("test");
        when(section.getString("item.material")).thenReturn("IRON_HOE");
        when(section.getString("shooting.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        String firearmId = "test";

        when(config.getFirearmTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(itemConfiguration.getSection(firearmId)).thenReturn(section);

        FirearmFactory firearmFactory = new FirearmFactory(config, itemConfiguration, fireModeFactory, internals, recoilSystemFactory, reloadSystemFactory);
        Firearm firearm = firearmFactory.make(context, firearmId);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());
    }

    @Test
    public void createFirearmWithScopeFromConfiguration() {
        Section scopeSection = mock(Section.class);
        when(scopeSection.getFloat("magnifications")).thenReturn(-0.1f);
        when(scopeSection.getString("stop-use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");
        when(scopeSection.getString("use-sound")).thenReturn("ENTITY_BLAZE_HURT-1-1-0");

        Section section = mock(Section.class);
        when(section.getSection("scope")).thenReturn(scopeSection);
        when(section.getString("description")).thenReturn("test");
        when(section.getString("display-name")).thenReturn("test");
        when(section.getString("item.material")).thenReturn("IRON_HOE");
        when(section.getString("shooting.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        String firearmId = "test";

        when(config.getFirearmTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(itemConfiguration.getSection(firearmId)).thenReturn(section);

        FirearmFactory firearmFactory = new FirearmFactory(config, itemConfiguration, fireModeFactory, internals, recoilSystemFactory, reloadSystemFactory);
        Firearm firearm = firearmFactory.make(context, firearmId);

        assertNotNull(firearm);
        assertEquals("test", firearm.getName());
        assertNotNull(firearm.getScopeAttachment());
    }

    @Test(expected = InvalidBattleItemFormatException.class)
    public void returnsErrorWhenConfigurationDoesNotHaveWeaponId() {
        FirearmFactory firearmFactory = new FirearmFactory(config, itemConfiguration, fireModeFactory, internals, recoilSystemFactory, reloadSystemFactory);
        firearmFactory.make(context,"unknown");
    }
}
