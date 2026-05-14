package nl.matsgemmeke.battlegrounds.item.gun;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.RangeProfile;
import nl.matsgemmeke.battlegrounds.item.recoil.Recoil;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeAttachment;
import nl.matsgemmeke.battlegrounds.item.scope.ScopeUser;
import nl.matsgemmeke.battlegrounds.item.shoot.ShootHandler;
import nl.matsgemmeke.battlegrounds.item.shoot.ShotPerformer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class DefaultGun extends BaseWeapon implements Gun {

    @Nullable
    private GunUser user;
    @Nullable
    private ItemTemplate itemTemplate;
    private RangeProfile rangeProfile;
    @Nullable
    private Recoil recoil;
    private ReloadSystem reloadSystem;
    private ResourceContainer resourceContainer;
    @Nullable
    private ScopeAttachment scopeAttachment;
    private ShootHandler shootHandler;

    @Override
    public ResourceContainer getResourceContainer() {
        return resourceContainer;
    }

    @Override
    public void setResourceContainer(ResourceContainer resourceContainer) {
        this.resourceContainer = resourceContainer;
    }

    @Nullable
    public ItemTemplate getItemTemplate() {
        return itemTemplate;
    }

    public void setItemTemplate(@Nullable ItemTemplate itemTemplate) {
        this.itemTemplate = itemTemplate;
    }

    @NotNull
    public RangeProfile getRangeProfile() {
        return rangeProfile;
    }

    public void setRangeProfile(@NotNull RangeProfile rangeProfile) {
        this.rangeProfile = rangeProfile;
    }

    @Nullable
    public Recoil getRecoil() {
        return recoil;
    }

    public void setRecoil(@Nullable Recoil recoil) {
        this.recoil = recoil;
    }

    @NotNull
    public ReloadSystem getReloadSystem() {
        return reloadSystem;
    }

    public void setReloadSystem(@NotNull ReloadSystem reloadSystem) {
        this.reloadSystem = reloadSystem;
    }

    @Nullable
    public ScopeAttachment getScopeAttachment() {
        return scopeAttachment;
    }

    public void setScopeAttachment(@Nullable ScopeAttachment scopeAttachment) {
        this.scopeAttachment = scopeAttachment;
    }

    public ShootHandler getShootHandler() {
        return shootHandler;
    }

    public void setShootHandler(ShootHandler shootHandler) {
        this.shootHandler = shootHandler;
    }

    @Nullable
    public GunUser getUser() {
        return user;
    }

    public void setUser(@Nullable GunUser user) {
        this.user = user;
    }

    public boolean applyScope(@NotNull ScopeUser scopeUser) {
        return scopeAttachment != null && scopeAttachment.applyEffect(scopeUser);
    }

    public boolean canShoot() {
        return resourceContainer.getLoadedAmount() > 0;
    }

    public boolean cancelReload() {
        return reloadSystem.cancelReload();
    }

    public boolean cancelScope() {
        return scopeAttachment != null && scopeAttachment.removeEffect();
    }

    public void cancelShooting() {
        shootHandler.cancel();
    }

    public boolean changeScopeMagnification() {
        return scopeAttachment != null && scopeAttachment.nextMagnification();
    }

    public int getRateOfFire() {
        return shootHandler.getRateOfFire();
    }

    public boolean isMatching(ItemStack itemStack) {
        return itemTemplate != null && itemTemplate.matchesTemplate(itemStack);
    }

    public boolean isReloadAvailable() {
        return !reloadSystem.isPerforming()
                && resourceContainer.getLoadedAmount() < resourceContainer.getCapacity()
                && resourceContainer.getReserveAmount() > 0;
    }

    public boolean isReloading() {
        return reloadSystem.isPerforming();
    }

    public boolean isShooting() {
        return shootHandler.isShooting();
    }

    public boolean isUsingScope() {
        return scopeAttachment != null && scopeAttachment.isScoped();
    }

    @Override
    public void reload(ReloadPerformer performer) {
        reloadSystem.performReload(performer, () -> {
            this.update();
            performer.setHeldItem(itemStack);
        });
    }

    @Override
    public void shoot(ShotPerformer performer) {
        shootHandler.shoot(performer);
    }

    public boolean update() {
        if (itemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.getTemplateValues();
        itemStack = itemTemplate.createItemStack(values);
        return true;
    }

    private Map<String, Object> getTemplateValues() {
        return Map.of(
                "name", name,
                "magazine_ammo", resourceContainer.getLoadedAmount(),
                "reserve_ammo", resourceContainer.getReserveAmount()
        );
    }
}
