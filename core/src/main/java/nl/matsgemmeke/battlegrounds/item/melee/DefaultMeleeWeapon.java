package nl.matsgemmeke.battlegrounds.item.melee;

import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.ItemTemplate;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
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
    private ItemController<MeleeWeaponUser> controller;
    @Nullable
    private ItemTemplate displayItemTemplate;
    @Nullable
    private ItemTemplate throwItemTemplate;
    @Nullable
    private MeleeWeaponUser user;
    private ReloadSystem reloadSystem;
    private ResourceContainer resourceContainer;
    @Nullable
    private ThrowHandler throwHandler;

    public DefaultMeleeWeapon() {
        this.controller = new ItemController<>();
    }

    @Override
    public double getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(double attackDamage) {
        this.attackDamage = attackDamage;
    }

    public ItemController<MeleeWeaponUser> getController() {
        return controller;
    }

    public void setController(ItemController<MeleeWeaponUser> controller) {
        this.controller = controller;
    }

    public Optional<ItemTemplate> getDisplayItemTemplate() {
        return Optional.ofNullable(displayItemTemplate);
    }

    public void setDisplayItemTemplate(@Nullable ItemTemplate displayItemTemplate) {
        this.displayItemTemplate = displayItemTemplate;
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

    @Override
    public Optional<MeleeWeaponUser> getUser() {
        return Optional.ofNullable(user);
    }

    public void configureThrowHandler(ThrowHandler throwHandler) {
        this.throwHandler = throwHandler;
    }

    @Override
    public void assign(MeleeWeaponUser user) {
        this.user = user;
    }

    @Override
    public void unassign() {
        user = null;
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
        if (user == null) {
            return;
        }

        controller.cancelAllFunctions();
        controller.performAction(Action.DROP_ITEM, user);
    }

    @Override
    public void onLeftClick() {
        this.performControlAction(Action.LEFT_CLICK);
    }

    @Override
    public void onPickUp(@NotNull MeleeWeaponUser user) {
        controller.performAction(Action.PICKUP_ITEM, user);
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
        if (user == null) {
            return;
        }

        controller.performAction(action, user);
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

        if (resourceContainer.getLoadedAmount() + resourceContainer.getReserveAmount() <= 0) {
            // The melee weapon is out of resources, so we unassign the user
            user = null;
        }
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
