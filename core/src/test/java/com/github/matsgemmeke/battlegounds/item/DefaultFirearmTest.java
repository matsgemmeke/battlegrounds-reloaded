package com.github.matsgemmeke.battlegounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.GameEntity;
import com.github.matsgemmeke.battlegrounds.api.entity.WeaponHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.game.GameSound;
import com.github.matsgemmeke.battlegrounds.api.item.OperatingMode;
import com.github.matsgemmeke.battlegrounds.api.item.ScopeAttachment;
import com.github.matsgemmeke.battlegrounds.item.DefaultFirearm;
import com.github.matsgemmeke.battlegrounds.item.mechanics.FireMode;
import com.github.matsgemmeke.battlegrounds.item.mechanics.ReloadSystem;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultFirearmTest {

    private GameContext context;
    private WeaponHolder holder;

    @Before
    public void setUp() {
        this.context = mock(GameContext.class);
        this.holder = mock(WeaponHolder.class);
    }

    @Test
    public void shouldNotMatchIfItemStackIsNull() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        DefaultFirearm firearm = new DefaultFirearm(context);

        assertFalse(firearm.isMatching(other));
    }

    @Test
    public void shouldNotMatchIfItemStackIsNotSimilar() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemStack itemStack = mock(ItemStack.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setItemStack(itemStack);

        assertFalse(firearm.isMatching(other));
    }

    @Test
    public void shouldMatchIfItemStackIsSimilar() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemStack itemStack = mock(ItemStack.class);
        when(itemStack.isSimilar(other)).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setItemStack(itemStack);

        assertTrue(firearm.isMatching(other));
    }

    @Test
    public void doesNothingWhenChangingHeldItemWithoutReloadSystem() {
        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.onChangeHeldItem(holder);
    }

    @Test
    public void cancelsCurrentOperatingModeWhenChangingHeldItem() {
        OperatingMode operatingMode = mock(OperatingMode.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setOperatingMode(operatingMode);
        firearm.onChangeHeldItem(holder);

        verify(operatingMode, times(1)).cancel(holder);
    }

    @Test
    public void activatesReloadSystemWhenLeftClicked() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setMagazineAmmo(0);
        firearm.setMagazineSize(30);
        firearm.setOperatingMode(null);
        firearm.setReloadSystem(reloadSystem);
        firearm.setReserveAmmo(30);
        firearm.onLeftClick(holder);

        verify(reloadSystem, times(1)).activate(holder);
    }

    @Test
    public void doesNotActivateReloadSystemWhenFirearmHasOngoingOperation() {
        OperatingMode operatingMode = mock(OperatingMode.class);
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setOperatingMode(operatingMode);
        firearm.setReloadSystem(reloadSystem);
        firearm.onLeftClick(holder);

        verify(reloadSystem, never()).activate(holder);
    }

    @Test
    public void removesScopeFromPlayerWhenFirearmHasScopeAndIsUsingIt() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);

        when(scopeAttachment.isScoped()).thenReturn(true);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setReloadSystem(reloadSystem);
        firearm.setScopeAttachment(scopeAttachment);
        firearm.onLeftClick(holder);

        verify(reloadSystem, never()).activate(holder);
        verify(scopeAttachment, times(1)).removeEffect();
    }

    @Test
    public void doesNotActivateReloadSystemWhenMagazineIsAlreadyFull() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setMagazineAmmo(30);
        firearm.setMagazineSize(30);
        firearm.setOperatingMode(null);
        firearm.setReloadSystem(reloadSystem);
        firearm.onLeftClick(holder);

        verify(reloadSystem, never()).activate(holder);
    }

    @Test
    public void doesNotActivateReloadSystemWhenThereIsNoReserveAmmo() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setMagazineAmmo(0);
        firearm.setMagazineSize(30);
        firearm.setOperatingMode(null);
        firearm.setReloadSystem(reloadSystem);
        firearm.setReserveAmmo(0);
        firearm.onLeftClick(holder);

        verify(reloadSystem, never()).activate(holder);
    }

    @Test
    public void executesShootActionWhenRightClicked() {
        FireMode fireMode = mock(FireMode.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setFireMode(fireMode);
        firearm.setMagazineAmmo(10);
        firearm.onRightClick(holder);

        verify(fireMode).activate(holder);
    }

    @Test
    public void activatesScopeAttachmentWhenFirearmHasOne() {
        FireMode fireMode = mock(FireMode.class);
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setFireMode(fireMode);
        firearm.setScopeAttachment(scopeAttachment);
        firearm.onRightClick(holder);

        verify(fireMode, never()).activate(holder);
        verify(scopeAttachment, times(1)).applyEffect(holder);
    }

    @Test
    public void playsTriggerSoundWhenRightClickingWithEmptyMagazine() {
        List<GameSound> triggerSounds = Collections.emptyList();
        LivingEntity entity = mock(LivingEntity.class);

        when(holder.getEntity()).thenReturn(entity);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setTriggerSounds(triggerSounds);
        firearm.onRightClick(holder);

        verify(context).playSounds(eq(triggerSounds), any());
    }

    @Test
    public void canShootProjectilesAtShortDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 10.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setShortDamage(100.0);
        firearm.setShortRange(10.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(100.0);
    }

    @Test
    public void canShootProjectilesAtMediumDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 50.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setMediumDamage(50.0);
        firearm.setMediumRange(50.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(50.0);
    }

    @Test
    public void canShootProjectilesAtLongDistanceTarget() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 1.0, 100.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setLongDamage(10.0);
        firearm.setLongRange(100.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(10.0);
    }

    @Test
    public void canShootProjectilesAtTargetWithHeadshotDamageMultiplier() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 2.0, 1.0, 0.0F, 0.0F);
        Location targetLocation = new Location(world, 1.0, 0.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        LivingEntity targetEntity = mock(LivingEntity.class);
        when(targetEntity.getLocation()).thenReturn(targetLocation);

        GameEntity target = mock(GameEntity.class);
        when(target.getEntity()).thenReturn(targetEntity);

        List<GameEntity> targets = Collections.singletonList(target);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(targets);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHeadshotDamageMultiplier(1.5);
        firearm.setHolder(holder);
        firearm.setShortDamage(100.0);
        firearm.setShortRange(10.0);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(target).damage(150.0);
    }

    @Test
    public void canShootProjectilesAtSolidBlock() {
        List<GameSound> shotSounds = Collections.emptyList();

        Block block = mock(Block.class);
        World world = mock(World.class);

        when(block.getWorld()).thenReturn(world);
        when(block.getType()).thenReturn(Material.STONE);
        when(world.getBlockAt(any())).thenReturn(block);

        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        when(context.producesCollisionAt(any(), any())).thenReturn(true);

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(world).playEffect(any(), eq(Effect.STEP_SOUND), eq(Material.STONE));
    }

    @Test
    public void displaysParticlesAtProjectileTrajectory() {
        List<GameSound> shotSounds = Collections.emptyList();

        World world = mock(World.class);
        Location holderLocation = new Location(world, 1.0, 1.0, 1.0, 0.0F, 0.0F);

        LivingEntity holderEntity = mock(LivingEntity.class);
        when(holderEntity.getEyeLocation()).thenReturn(holderLocation);
        when(holderEntity.getLocation()).thenReturn(holderLocation);

        when(context.getTargets(any(), any(), anyDouble())).thenReturn(Collections.emptyList());

        when(holder.getEntity()).thenReturn(holderEntity);
        when(holder.getRelativeAccuracy()).thenReturn(2.0f);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setShotSounds(shotSounds);
        firearm.shoot();

        verify(context).playSounds(eq(shotSounds), any(Location.class));
        verify(world).spawnParticle(eq(Particle.REDSTONE), any(), eq(1), eq(0.0), eq(0.0), eq(0.0), eq(0.0), any());
    }

    @Test
    public void canNotReloadWithoutHolder() {
        DefaultFirearm firearm = new DefaultFirearm(context);
        boolean reloaded = firearm.reload();

        assertFalse(reloaded);
    }

    @Test
    public void canNotShootProjectilesWithoutHolder() {
        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.shoot();

        verify(context, never()).playSounds(any(), any());
    }

    @Test
    public void resetsOperatingModeAndHolderWhenDropped() {
        OperatingMode operatingMode = mock(OperatingMode.class);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setOperatingMode(operatingMode);
        firearm.onDrop(holder);

        verify(operatingMode, times(1)).cancel(holder);

        assertNull(firearm.getHolder());
    }

    @Test
    public void doesNotUpdateIfFirearmHasNoItemStack() {
        DefaultFirearm firearm = new DefaultFirearm(context);
        boolean updated = firearm.update();

        assertFalse(updated);
    }

    @Test
    public void updatingItemChangesItsDisplayName() {
        ItemStack itemStack = mock(ItemStack.class);
        ItemMeta itemMeta = mock(ItemMeta.class);

        when(holder.updateItemStack(itemStack)).thenReturn(true);
        when(itemStack.getItemMeta()).thenReturn(itemMeta);

        DefaultFirearm firearm = new DefaultFirearm(context);
        firearm.setHolder(holder);
        firearm.setItemStack(itemStack);
        firearm.setMagazineAmmo(10);
        firearm.setName("name");
        firearm.setReserveAmmo(20);

        boolean updated = firearm.update();

        verify(itemMeta, times(1)).setDisplayName(ChatColor.WHITE + "name 10/20");

        assertTrue(updated);
    }
}
