package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionBinding;
import nl.matsgemmeke.battlegrounds.item.controls.Function;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import nl.matsgemmeke.battlegrounds.util.Procedure;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        gun.setResourceContainer(resourceContainer);
        gun.setReloadSystem(reloadSystem);
        boolean reloadAvailable = gun.isReloadAvailable();

        assertThat(reloadAvailable).isFalse();
    }

    @Test
    void isReloadAvailableReturnsFalseWhenNoReserveAmmoIsAvailable() {
        ResourceContainer resourceContainer = new ResourceContainer(0, 30, 0, 300);

        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        when(reloadSystem.isPerforming()).thenReturn(false);

        gun.setResourceContainer(resourceContainer);
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
        Function<GunUser> function1 = mock();
        when(function1.isPerforming()).thenReturn(true);

        Function<GunUser> function2 = mock();
        ActionBinding<GunUser> binding1 = new ActionBinding<>(function1, 1, false, false, false);
        ActionBinding<GunUser> binding2 = new ActionBinding<>(function2, 1, false, false, false);

        gun.getController().bind(Action.LEFT_CLICK, binding1);
        gun.getController().bind(Action.CHANGE_FROM, binding2);
        gun.onChangeFrom();

        verify(function1).cancel();
        verify(function2, never()).perform(any());
    }

    @Test
    @DisplayName("onChangeFrom performs action on controller when user is not null")
    void onChangeFrom_performsChangeFromAction() {
        GunUser user = mock(GunUser.class);
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.CHANGE_FROM, binding);
        gun.setUser(user);
        gun.onChangeFrom();

        verify(function).perform(user);
    }

    @Test
    @DisplayName("onLeftClick does not interact with controller when user is null")
    void onLeftClick_nullUser() {
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.LEFT_CLICK, binding);
        gun.onLeftClick();

        verifyNoInteractions(function);
    }

    @Test
    @DisplayName("onLeftClick performs action on controller when user is not null")
    void onLeftClick_performsLeftClickAction() {
        GunUser user = mock(GunUser.class);
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.LEFT_CLICK, binding);
        gun.setUser(user);
        gun.onLeftClick();

        verify(function).perform(user);
    }

    @Test
    @DisplayName("onRightClick does not interact with controller when user is null")
    void onRightClick_nullUser() {
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.RIGHT_CLICK, binding);
        gun.onRightClick();

        verifyNoInteractions(function);
    }

    @Test
    @DisplayName("onRightClick performs action on controller when user is not null")
    void onRightClick_performsRightClickAction() {
        GunUser user = mock(GunUser.class);
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.RIGHT_CLICK, binding);
        gun.setUser(user);
        gun.onRightClick();

        verify(function).perform(user);
    }

    @Test
    @DisplayName("onSwapFrom does not interact with controller when user is null")
    void onSwapFrom_nullUser() {
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.SWAP_FROM, binding);
        gun.onSwapFrom();

        verifyNoInteractions(function);
    }

    @Test
    @DisplayName("onSwapFrom performs action on controller when user is not null")
    void onSwapFrom_performsSwapFromAction() {
        GunUser user = mock(GunUser.class);
        Function<GunUser> function = mock();
        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.SWAP_FROM, binding);
        gun.setUser(user);
        gun.onSwapFrom();

        verify(function).perform(user);
    }

    @Test
    void reloadActivatesReloadSystemWithGivenPerformerAndUpdatesItem() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);
        GunUser user = mock(GunUser.class);
        ItemStack itemStack = new ItemStack(Material.IRON_HOE);
        ReloadSystem reloadSystem = mock(ReloadSystem.class);
        ReloadPerformer performer = mock(ReloadPerformer.class);

        ItemTemplate itemTemplate = mock(ItemTemplate.class);
        when(itemTemplate.createItemStack(any())).thenReturn(itemStack);

        gun.setUser(user);
        gun.setItemTemplate(itemTemplate);
        gun.setName("test");
        gun.setReloadSystem(reloadSystem);
        gun.setResourceContainer(resourceContainer);
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
        ResourceContainer resourceContainer = new ResourceContainer(30, 30, 90, 300);

        gun.setResourceContainer(resourceContainer);
        boolean canShoot = gun.canShoot();

        assertThat(canShoot).isTrue();
    }

    @Test
    void canShootReturnsFalseWhenMagazineHasNoAmmo() {
        ResourceContainer resourceContainer = new ResourceContainer(30, 0, 90, 300);

        gun.setResourceContainer(resourceContainer);
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
    @DisplayName("onDrop cancels controller functions and unassigns user")
    void onDrop_cancelsFunctionAndUnassignsUser() {
        GunUser user = mock(GunUser.class);

        Function<GunUser> function = mock();
        when(function.isPerforming()).thenReturn(true);

        ActionBinding<GunUser> binding = new ActionBinding<>(function, 1, false, false, false);

        gun.getController().bind(Action.LEFT_CLICK, binding);
        gun.setUser(user);
        gun.onDrop();

        assertThat(gun.getUser()).isNull();

        verify(function).cancel();
    }

    @Test
    @DisplayName("onPickup assigns user")
    void onPickUp_assignsUser() {
        GunUser user = mock(GunUser.class);

        gun.onPickUp(user);

        assertThat(gun.getUser()).isEqualTo(user);
    }

    @Test
    void updateReturnsFalseWhenGunHasNoItemStack() {
        boolean updated = gun.update();

        assertThat(updated).isFalse();
    }
}
