package com.github.matsgemmeke.battlegrounds.item;

import com.github.matsgemmeke.battlegrounds.api.entity.ItemHolder;
import com.github.matsgemmeke.battlegrounds.api.game.GameContext;
import com.github.matsgemmeke.battlegrounds.api.item.Gun;
import com.github.matsgemmeke.battlegrounds.api.item.ScopeAttachment;
import com.github.matsgemmeke.battlegrounds.item.mechanics.ReloadSystem;
import com.github.matsgemmeke.battlegrounds.item.recoil.RecoilSystem;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGun extends AbstractWeapon implements Gun {

    protected double accuracy;
    protected double damageAmplifier;
    protected double longDamage;
    protected double longRange;
    protected double mediumDamage;
    protected double mediumRange;
    protected double recoilAmplifier;
    protected double shortDamage;
    protected double shortRange;
    @Nullable
    protected RecoilSystem recoilSystem;
    @Nullable
    protected ReloadSystem reloadSystem;
    @Nullable
    protected ScopeAttachment scopeAttachment;

    public AbstractGun(@NotNull GameContext context) {
        super(context);
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getDamageAmplifier() {
        return damageAmplifier;
    }

    public void setDamageAmplifier(double damageAmplifier) {
        this.damageAmplifier = damageAmplifier;
    }

    public double getLongDamage() {
        return longDamage;
    }

    public void setLongDamage(double longDamage) {
        this.longDamage = longDamage;
    }

    public double getLongRange() {
        return longRange;
    }

    public void setLongRange(double longRange) {
        this.longRange = longRange;
    }

    public double getMediumDamage() {
        return mediumDamage;
    }

    public void setMediumDamage(double mediumDamage) {
        this.mediumDamage = mediumDamage;
    }

    public double getMediumRange() {
        return mediumRange;
    }

    public void setMediumRange(double mediumRange) {
        this.mediumRange = mediumRange;
    }

    @Nullable
    public RecoilSystem getRecoilSystem() {
        return recoilSystem;
    }

    public void setRecoilSystem(@Nullable RecoilSystem recoilSystem) {
        this.recoilSystem = recoilSystem;
    }

    @Nullable
    public ReloadSystem getReloadSystem() {
        return reloadSystem;
    }

    public void setReloadSystem(@Nullable ReloadSystem reloadSystem) {
        this.reloadSystem = reloadSystem;
    }

    @Nullable
    public ScopeAttachment getScopeAttachment() {
        return scopeAttachment;
    }

    public void setScopeAttachment(@Nullable ScopeAttachment scopeAttachment) {
        this.scopeAttachment = scopeAttachment;
    }

    public double getShortDamage() {
        return shortDamage;
    }

    public void setShortDamage(double shortDamage) {
        this.shortDamage = shortDamage;
    }

    public double getShortRange() {
        return shortRange;
    }

    public void setShortRange(double shortRange) {
        this.shortRange = shortRange;
    }

    @NotNull
    protected abstract String getItemDisplayName();

    public void onDrop(@NotNull ItemHolder holder) {
        if (operatingMode != null) {
            operatingMode.cancel(holder);
            holder.applyOperatingState(false);
        }

        holder.removeItem(this);

        this.holder = null;
    }

    public boolean reload() {
        if (holder == null) {
            return false;
        }

        return reloadSystem != null && reloadSystem.activate(holder);
    }

    public boolean update() {
        if (itemStack == null || itemStack.getItemMeta() == null) {
            return false;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(this.getItemDisplayName());

        itemStack.setItemMeta(itemMeta);

        return holder != null && holder.updateItemStack(itemStack);
    }
}
