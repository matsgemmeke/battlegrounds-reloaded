package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadPerformer;
import nl.matsgemmeke.battlegrounds.item.reload.ReloadSystem;
import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowHandler;
import nl.matsgemmeke.battlegrounds.item.throwing.ThrowPerformer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DefaultMeleeWeapon extends BaseWeapon implements MeleeWeapon {

    private double attackDamage;
    private ItemControls<MeleeWeaponHolder> controls;
    @Nullable
    private ItemTemplate displayItemTemplate;
    @Nullable
    private ItemTemplate throwItemTemplate;
    @Nullable
    private MeleeWeaponHolder holder;
    private ReloadSystem reloadSystem;
    private ResourceContainer resourceContainer;
    @Nullable
    private ThrowHandler throwHandler;

    public DefaultMeleeWeapon() {
        this.controls = new ItemControls<>();
    }

    @Override
    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public ItemControls<MeleeWeaponHolder> getControls() {
        return controls;
    }

    public void setControls(ItemControls<MeleeWeaponHolder> controls) {
        this.controls = controls;
    }

    public Optional<ItemTemplate> getDisplayItemTemplate() {
        return Optional.ofNullable(displayItemTemplate);
    }

    public void setDisplayItemTemplate(@Nullable ItemTemplate displayItemTemplate) {
        this.displayItemTemplate = displayItemTemplate;
    }

    @Override
    public Optional<MeleeWeaponHolder> getHolder() {
        return Optional.ofNullable(holder);
    }

    @Override
    public void setHolder(@Nullable MeleeWeaponHolder holder) {
        this.holder = holder;
    }

    public ReloadSystem getReloadSystem() {
        return reloadSystem;
    }

    public void setReloadSystem(ReloadSystem reloadSystem) {
        this.reloadSystem = reloadSystem;
    }

    @Override
    public ResourceContainer getResourceContainer() {
        return resourceContainer;
    }

    @Override
    public void setResourceContainer(ResourceContainer resourceContainer) {
        this.resourceContainer = resourceContainer;
    }

    public void configureThrowHandler(ThrowHandler throwHandler) {
        this.throwHandler = throwHandler;
    }

    @Override
    public boolean isMatching(@NotNull ItemStack itemStack) {
        return displayItemTemplate != null && displayItemTemplate.matchesTemplate(itemStack);
    }

    @Override
    public void onChangeFrom() {
        this.performControlAction(Action.CHANGE_FROM);
    }

    @Override
    public void onChangeTo() {
        this.performControlAction(Action.CHANGE_TO);
    }

    @Override
    public void onDrop() {
        if (holder == null) {
            return;
        }

        controls.cancelAllFunctions();
        controls.performAction(Action.DROP_ITEM, holder);
        holder = null;
    }

    @Override
    public void onLeftClick() {
        this.performControlAction(Action.LEFT_CLICK);
    }

    @Override
    public void onPickUp(@NotNull MeleeWeaponHolder holder) {
        this.holder = holder;

        controls.performAction(Action.PICKUP_ITEM, holder);
    }

    @Override
    public void onRightClick() {
        this.performControlAction(Action.RIGHT_CLICK);
    }

    @Override
    public void onSwapFrom() {
        this.performControlAction(Action.SWAP_FROM);
    }

    @Override
    public void onSwapTo() {
        this.performControlAction(Action.SWAP_TO);
    }

    private void performControlAction(Action action) {
        if (holder == null) {
            return;
        }

        controls.performAction(action, holder);
    }

    @Override
    public boolean cancelReload() {
        return reloadSystem.cancelReload();
    }

    @Override
    public boolean isReloadAvailable() {
        return !reloadSystem.isPerforming()
                && resourceContainer.getLoadedAmount() < resourceContainer.getCapacity()
                && resourceContainer.getReserveAmount() > 0;
    }

    @Override
    public boolean isReloading() {
        return reloadSystem.isPerforming();
    }

    @Override
    public void reload(ReloadPerformer performer) {
        reloadSystem.performReload(performer, () -> {
            this.update();
            performer.setHeldItem(itemStack);
        });
    }

    @Override
    public void performThrow(ThrowPerformer performer) {
        if (throwHandler == null) {
            return;
        }

        throwHandler.performThrow(performer);
    }

    @Override
    public boolean update() {
        if (displayItemTemplate == null) {
            return false;
        }

        Map<String, Object> values = this.createTemplateValues();
        itemStack = displayItemTemplate.createItemStack(values);
        return true;
    }

    private Map<String, Object> createTemplateValues() {
        Map<String, Object> values = new HashMap<>();

        if (name != null) {
            values.put("name", name);
            values.put("loaded_amount", resourceContainer.getLoadedAmount());
            values.put("reserve_amount", resourceContainer.getReserveAmount());
        }

        return values;
    }
}
