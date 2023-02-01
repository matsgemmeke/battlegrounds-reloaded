package com.github.matsgemmeke.battlegounds.item.factory;

import com.github.matsgemmeke.battlegrounds.api.configuration.BattlegroundsConfig;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.configuration.BattleItemConfiguration;
import com.github.matsgemmeke.battlegrounds.item.factory.FiringModeFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.GunFactory;
import com.github.matsgemmeke.battlegrounds.item.factory.InvalidBattleItemFormatException;
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
public class GunFactoryTest {

    private BattleContext context;
    private BattlegroundsConfig config;
    private BattleItemConfiguration itemConfiguration;
    private FiringModeFactory firingModeFactory;

    @Before
    public void setUp() {
        this.context = mock(BattleContext.class);
        this.config = mock(BattlegroundsConfig.class);
        this.itemConfiguration = mock(BattleItemConfiguration.class);
        this.firingModeFactory = mock(FiringModeFactory.class);

        PowerMockito.mockStatic(Bukkit.class);
        ItemFactory itemFactory = mock(ItemFactory.class);
        when(Bukkit.getItemFactory()).thenReturn(itemFactory);
    }

    @Test
    public void canCreateGunFromConfiguration() {
        Section section = mock(Section.class);
        when(section.getString("description")).thenReturn("test");
        when(section.getString("display-name")).thenReturn("test");
        when(section.getString("item.material")).thenReturn("IRON_HOE");
        when(section.getString("sound.shot-sound")).thenReturn("ENTITY_BLAZE_HURT-3-2-0");

        String gunId = "test";

        when(config.getFirearmTriggerSound()).thenReturn("ENTITY_BLAZE_HURT-3-2-0");
        when(itemConfiguration.getSection(gunId)).thenReturn(section);

        GunFactory gunFactory = new GunFactory(config, itemConfiguration, firingModeFactory);
        Gun gun = gunFactory.make(context, gunId);

        assertNotNull(gun);
    }

    @Test(expected = InvalidBattleItemFormatException.class)
    public void returnsErrorWhenConfigurationDoesNotHaveWeaponId() {
        GunFactory gunFactory = new GunFactory(config, itemConfiguration, firingModeFactory);
        gunFactory.make(context,"unknown");
    }
}
