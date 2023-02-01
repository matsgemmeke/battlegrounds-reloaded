package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.item.DefaultGun;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultGunTest {

    private BattleContext context;
    private BattleEntity holder;
    private String description;
    private String id;
    private String name;

    @Before
    public void setUp() {
        this.context = mock(BattleContext.class);
        this.holder = mock(BattleEntity.class);
        this.description = "description";
        this.id = "id";
        this.name = "name";
    }

    @Test
    public void executesReloadActionWhenLeftClicked() {
        DefaultGun gun = new DefaultGun(id, name, description, context);
        gun.onLeftClick(holder);
    }

    @Test
    public void executesShootActionWhenRightClicked() {
        FiringMode firingMode = mock(FiringMode.class);

        DefaultGun gun = new DefaultGun(id, name, description, context);
        gun.setFiringMode(firingMode);
        gun.setMagazineAmmo(10);
        gun.onRightClick(holder);

        verify(firingMode).activate();
    }

    @Test
    public void playsTriggerSoundWhenRightClickingWithEmptyMagazine() {
        List<BattleSound> triggerSounds = Collections.emptyList();
        LivingEntity entity = mock(LivingEntity.class);

        when(holder.getEntity()).thenReturn(entity);

        DefaultGun gun = new DefaultGun(id, name, description, context);
        gun.setHolder(holder);
        gun.setTriggerSounds(triggerSounds);
        gun.onRightClick(holder);

        verify(context).playSounds(eq(triggerSounds), any(Location.class));
    }

    @Test
    public void a() {
        Tag tag = Tag.SLABS;

        System.out.println(tag);
    }

    @Test
    public void canShootProjectiles() {
        Block block = mock(Block.class);
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        when(world.getBlockAt(any(Location.class))).thenReturn(block);

        Location location = new Location(world, 1.0, 2.0, 3.0, 90.0F, 90.0F);

        LivingEntity entity = mock(LivingEntity.class);
        when(entity.getEyeLocation()).thenReturn(location);

        when(holder.getEntity()).thenReturn(entity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, description, context);
        gun.setAccuracy(0.5);
        gun.setHolder(holder);
        gun.setRecoilAmplifier(10.0);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
    }

    @Test
    public void canNotShootProjectilesWithoutHolder() {
        DefaultGun gun = new DefaultGun(id, name, description, context);
        gun.shoot();

        verify(context, never()).playSounds(any(), any());
    }
}
