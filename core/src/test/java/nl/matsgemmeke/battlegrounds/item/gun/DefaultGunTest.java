package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.reload.AmmunitionStorage;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultGunTest {

    private static final int RATE_OF_FIRE = 600;

    @InjectMocks
    private DefaultGun gun;

    @Test
    void applyScopeReturnsFalseWhenGunHasNoScopeAttachment() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        boolean applied = gun.applyScope(scopeUser);

        assertThat(applied).isFalse();
    }

    @Test
    void applyScopeReturnsTrueAndAppliesScopeAttachmentEffectToGivenScopeUser() {
        ScopeUser scopeUser = mock(ScopeUser.class);

        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.applyEffect(scopeUser)).thenReturn(true);

        gun.setScopeAttachment(scopeAttachment);
        boolean applied = gun.applyScope(scopeUser);

        assertThat(applied).isTrue();
    }

    @Test
    void cancelReloadCancelsReloadSystemPerformance() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.cancelReload()).thenReturn(true);

        gun.setReloadSystem(reloadSystem);
        boolean cancelled = gun.cancelReload();

        assertThat(cancelled).isTrue();
    }

    @Test
    void cancelScopeReturnsFalseWhenGunHasNoScopeAttachment() {
        boolean cancelled = gun.cancelScope();

        assertThat(cancelled).isFalse();
    }

    @Test
    void cancelScopeRemovesScopeAttachmentEffect() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.removeEffect()).thenReturn(true);

        gun.setScopeAttachment(scopeAttachment);
        boolean cancelled = gun.cancelScope();

        assertThat(cancelled).isTrue();
    }

    @Test
    void cancelShootingCycleCancelsShootHandler() {
        ShootHandler shootHandler = mock(ShootHandler.class);

        gun.setShootHandler(shootHandler);
        gun.cancelShooting();

        verify(shootHandler).cancel();
    }

    @Test
    void changeScopeMagnificationReturnsFalseWhenGunHasNoScopeAttachment() {
        boolean changed = gun.changeScopeMagnification();

        assertThat(changed).isFalse();
    }

    @Test
    void changeScopeMagnificationReturnsFalseWhenScopeAttachmentCannotChangeMagnification() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.nextMagnification()).thenReturn(false);

        gun.setScopeAttachment(scopeAttachment);
        boolean changed = gun.changeScopeMagnification();

        assertThat(changed).isFalse();
    }

    @Test
    void changeScopeMagnificationReturnsTrueWhenScopeAttachmentChangesMagnification() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.nextMagnification()).thenReturn(true);

        gun.setScopeAttachment(scopeAttachment);
        boolean changed = gun.changeScopeMagnification();

        assertThat(changed).isTrue();
    }

    @Test
    void getRateOfFireReturnsRateOfFireValueFromShootHandler() {
        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.getRateOfFire()).thenReturn(RATE_OF_FIRE);

        gun.setShootHandler(shootHandler);
        int result = gun.getRateOfFire();

        assertThat(result).isEqualTo(RATE_OF_FIRE);
    }

    @Test
    void isMatchingReturnsTrueWhenItemTemplateMatchesWithGivenItemStack() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(other)).thenReturn(true);

        gun.setItemTemplate(itemTemplate);
        boolean matches = gun.isMatching(other);

        assertThat(matches).isTrue();
    }

    @Test
    void isMatchingReturnsFalseWhenItemTemplateIsNull() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        gun.setItemTemplate(null);
        boolean matches = gun.isMatching(other);

        assertThat(matches).isFalse();
    }

    @Test
    public void isMatchingReturnsFalseWhenTemplateDoesNotMatchWithGivenItemStack() {
        ItemStack other = new ItemStack(Material.IRON_HOE);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.matchesTemplate(other)).thenReturn(false);

        gun.setItemTemplate(itemTemplate);
        boolean matches = gun.isMatching(other);

        assertThat(matches).isFalse();
    }

    @Test
    void isReloadAvailableReturnsFalseWhenReloadSystemIsPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(true);

        gun.setReloadSystem(reloadSystem);
        boolean reloadAvailable = gun.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @Test
    void isReloadAvailableReturnsFalseWhenMagazineAmmoEqualsMagazineSize() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        gun.setAmmunitionStorage(ammunitionStorage);
        gun.setReloadSystem(reloadSystem);
        boolean reloadAvailable = gun.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @Test
    void isReloadAvailableReturnsFalseWhenNoReserveAmmoIsAvailable() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 30, 0, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        gun.setAmmunitionStorage(ammunitionStorage);
        gun.setReloadSystem(reloadSystem);
        boolean reloadAvailable = gun.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @Test
    void isReloadingReturnsTrueWhenReloadSystemIsPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(true);

        gun.setReloadSystem(reloadSystem);
        boolean reloading = gun.isReloading();

        assertThat(reloading).isTrue();
    }

    @Test
    void isReloadingReturnsFalseWhenReloadSystemIsNotPerforming() {
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        gun.setReloadSystem(reloadSystem);
        boolean reloading = gun.isReloading();

        assertThat(reloading).isFalse();
    }

    @Test
    void isShootingReturnsTrueWhenShootHandlerIsShooting() {
        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.isShooting()).thenReturn(true);

        gun.setShootHandler(shootHandler);
        boolean shooting = gun.isShooting();

        assertThat(shooting).isTrue();
    }

    @Test
    void isShootingReturnsFalseWhenShootHandlerIsNotShooting() {
        ShootHandler shootHandler = mock(ShootHandler.class);
        when(shootHandler.isShooting()).thenReturn(false);

        gun.setShootHandler(shootHandler);
        boolean shooting = gun.isShooting();

        assertThat(shooting).isFalse();
    }

    @Test
    void isUsingScopeReturnsFalseWhenGunHasNoScopeAttachment() {
        boolean usingScope = gun.isUsingScope();

        assertThat(usingScope).isFalse();
    }

    @Test
    void isUsingScopeReturnsFalseWhenScopeAttachmentIsNotScoped() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.isScoped()).thenReturn(false);

        gun.setScopeAttachment(scopeAttachment);
        boolean usingScope = gun.isUsingScope();

        assertThat(usingScope).isFalse();
    }

    @Test
    void isUsingScopeReturnsTrueWhenScopeAttachmentIsScoped() {
        ScopeAttachment scopeAttachment = mock(ScopeAttachment.class);
        when(scopeAttachment.isScoped()).thenReturn(true);

        gun.setScopeAttachment(scopeAttachment);
        boolean usingScope = gun.isUsingScope();

        assertThat(usingScope).isTrue();
    }

    @Test
    void onChangeFromCancelsOngoingFunctions() {
        ItemFunction<GunHolder> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        ItemFunction<GunHolder> function2 = mock();

        gun.getControls().addControl(Action.LEFT_CLICK, function1);
        gun.getControls().addControl(Action.CHANGE_FROM, function2);
        gun.onChangeFrom();

        verify(function1).cancel();
        verify(function2, never()).perform(any());
    }

    @Test
    void onChangeFromExecutesCorrespondingFunctionWhenHolderIsNotNull() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        gun.getControls().addControl(Action.CHANGE_FROM, function);
        gun.setHolder(holder);
        gun.onChangeFrom();

        verify(function).perform(holder);
    }

    @Test
    void onLeftClickDoesNotInteractWithControlsWhenHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        gun.getControls().addControl(Action.LEFT_CLICK, function);
        gun.onLeftClick();

        verifyNoInteractions(function);
    }

    @Test
    void onLeftClickExecutesCorrespondingFunctionWhenHolderIsNotNull() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        gun.getControls().addControl(Action.LEFT_CLICK, function);
        gun.setHolder(holder);
        gun.onLeftClick();

        verify(function).perform(holder);
    }

    @Test
    void onRightClickDoesNotInteractWithControlsWhenHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        gun.getControls().addControl(Action.RIGHT_CLICK, function);
        gun.onRightClick();

        verifyNoInteractions(function);
    }

    @Test
    void onRightClickExecutesCorrespondingFunctionWhenHolderIsNotNull() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        gun.getControls().addControl(Action.RIGHT_CLICK, function);
        gun.setHolder(holder);
        gun.onRightClick();

        verify(function).perform(holder);
    }

    @Test
    void onSwapFromDoesNotInteractWithControlsWhenHolderIsNull() {
        ItemFunction<GunHolder> function = mock();

        gun.getControls().addControl(Action.SWAP_FROM, function);
        gun.onSwapFrom();

        verifyNoInteractions(function);
    }

    @Test
    void onSwapFromExecutesCorrespondingFunctionWhenHolderIsNotNull() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = mock();
        when(function.isAvailable()).thenReturn(true);

        gun.getControls().addControl(Action.SWAP_FROM, function);
        gun.setHolder(holder);
        gun.onSwapFrom();

        verify(function).perform(holder);
    }

    @Test
    void reloadActivatesReloadSystemWithGivenPerformerAndUpdatesItem() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);
        GunHolder holder = mock(GunHolder.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        gun.setAmmunitionStorage(ammunitionStorage);
        gun.setHolder(holder);
        gun.setItemTemplate(itemTemplate);
        gun.setName("test");
        gun.setReloadSystem(reloadSystem);
        gun.reload(performer);

        ArgumentCaptor<Procedure> procedureCaptor = ArgumentCaptor.forClass(Procedure.class);
        verify(reloadSystem).performReload(eq(performer), procedureCaptor.capture());

        Procedure onReload = procedureCaptor.getValue();
        onReload.apply();

        verify(performer).setHeldItem(itemStack);
        verify(reloadSystem).performReload(performer, onReload);
    }

    @Test
    void canShootReturnsTrueWhenMagazineHasAmmo() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(30, 30, 90, 300);

        gun.setAmmunitionStorage(ammunitionStorage);
        boolean canShoot = gun.canShoot();

        assertThat(canShoot).isTrue();
    }

    @Test
    void canShootReturnsFalseWhenMagazineHasNoAmmo() {
        AmmunitionStorage ammunitionStorage = new AmmunitionStorage(0, 30, 90, 300);

        gun.setAmmunitionStorage(ammunitionStorage);
        boolean canShoot = gun.canShoot();

        assertThat(canShoot).isFalse();
    }

    @Test
    public void shootDelegatesToShootHandler() {
        ShootHandler shootHandler = mock(ShootHandler.class);
        ShotPerformer performer = mock(ShotPerformer.class);

        gun.setShootHandler(shootHandler);
        gun.shoot(performer);

        verify(shootHandler).shoot(performer);
    }

    @Test
    void onDropCancelsFunctionsAndResetsHolderWhenDropped() {
        GunHolder holder = mock(GunHolder.class);

        ItemFunction<GunHolder> function = mock();
        when(function.isPerforming()).thenReturn(true);

        gun.getControls().addControl(Action.LEFT_CLICK, function);
        gun.setHolder(holder);
        gun.onDrop();

        assertThat(gun.getHolder()).isNull();

        verify(function).cancel();
    }

    @Test
    void onPickUpSetHolder() {
        GunHolder holder = mock(GunHolder.class);

        gun.onPickUp(holder);

        assertThat(gun.getHolder()).isEqualTo(holder);
    }

    @Test
    void updateReturnsFalseWhenGunHasNoItemStack() {
        boolean updated = gun.update();

        assertThat(updated).isFalse();
    }
}
