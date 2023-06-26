package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.BattleEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.BattleItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.BattleContext;
import com.github.matsgemmeke.battlegrounds.api.game.BattleSound;
import com.github.matsgemmeke.battlegrounds.item.DefaultGun;
import com.github.matsgemmeke.battlegrounds.item.mechanism.FiringMode;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class DefaultGunTest {

    private BattleContext context;
    private BattleItemHolder holder;
    private String id;
    private String name;

    @Before
    public void setUp() {
        this.context = mock(BattleContext.class);
        this.holder = mock(BattleItemHolder.class);
        this.id = "id";
        this.name = "name";
    }

    @Test
    public void executesReloadActionWhenLeftClicked() {
        DefaultGun gun = new DefaultGun(id, name, context);
        gun.onLeftClick(holder);
    }

    @Test
    public void executesShootActionWhenRightClicked() {
        FiringMode firingMode = mock(FiringMode.class);

        DefaultGun gun = new DefaultGun(id, name, context);
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

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setTriggerSounds(triggerSounds);
        gun.onRightClick(holder);

        verify(context).playSounds(eq(triggerSounds), any(Location.class));
    }

    @Test
    public void canShootProjectilesAtShortDistanceTarget() {
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 10.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        BattleEntity target = mock(BattleEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<BattleEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setShortDamage(100.0);
        gun.setShortRange(10.0);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(100.0);
    }

    @Test
    public void canShootProjectilesAtMediumDistanceTarget() {
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 50.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        BattleEntity target = mock(BattleEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<BattleEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setMediumDamage(50.0);
        gun.setMediumRange(50.0);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(50.0);
    }

    @Test
    public void canShootProjectilesAtLongDistanceTarget() {
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 100.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        BattleEntity target = mock(BattleEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<BattleEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setLongDamage(10.0);
        gun.setLongRange(100.0);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(10.0);
    }

    @Test
    public void canShootProjectilesAtTargetWithHeadshotDamageMultiplier() {
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 2.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 0.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        BattleEntity target = mock(BattleEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<BattleEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHeadshotDamageMultiplier(1.5);
        gun.setHolder(holder);
        gun.setShortDamage(100.0);
        gun.setShortRange(10.0);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(150.0);
    }

    @Test
    public void canShootProjectilesAtSolidBlock() {
        List<BattleSound> shotSounds = Collections.emptyList();

        Block block = mock(Block.class);
        World world = mock(World.class);

        when(block.getWorld()).thenReturn(world);
        when(block.getType()).thenReturn(Material.STONE);
        when(world.getBlockAt(any())).thenReturn(block);

        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        when(context.producesCollisionAt(any())).thenReturn(true);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(world).playEffect(any(), eq(Effect.STEP_SOUND), eq(Material.STONE));
    }

    @Test
    public void displaysParticlesAtProjectileTrajectory() {
        List<BattleSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(Collections.emptyList());

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0);

        DefaultGun gun = new DefaultGun(id, name, context);
        gun.setHolder(holder);
        gun.setShotSounds(shotSounds);
        gun.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(world).spawnParticle(eq(Particle.REDSTONE), any(), eq(1), eq(0.0), eq(0.0), eq(0.0), eq(0.0), any());
    }

    @Test
    public void canNotShootProjectilesWithoutHolder() {
        DefaultGun gun = new DefaultGun(id, name, context);
        gun.shoot();

        verify(context, never()).playSounds(any(), any());
    }
}
