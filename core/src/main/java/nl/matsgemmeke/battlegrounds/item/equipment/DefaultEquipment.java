package nl.matsgemmeke.battlegrounds.item.equipment;

import nl.matsgemmeke.battlegrounds.game.GameContext;
import nl.matsgemmeke.battlegrounds.item.BaseWeapon;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ItemControls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultEquipment extends BaseWeapon implements Equipment {

    @Nullable
    private EquipmentHolder holder;
    @NotNull
    private ItemControls<EquipmentHolder> controls;

    public DefaultEquipment(@NotNull GameContext context) {
        super(context);
        this.controls = new ItemControls<>();
    }

    @NotNull
    public ItemControls<EquipmentHolder> getControls() {
        return controls;
    }

    @Nullable
    public EquipmentHolder getHolder() {
        return holder;
    }

    public void setHolder(@Nullable EquipmentHolder holder) {
        this.holder = holder;
    }

    public void onChangeFrom() {
    }

    public void onChangeTo() {
    }

    public void onDrop() {
    }

    public void onLeftClick() {
        if (holder == null) {
            return;
        }

        controls.performAction(Action.LEFT_CLICK, holder);
    }

    public void onPickUp(@NotNull EquipmentHolder holder) {
    }

    public void onRightClick() {
    }

    public void onSwapFrom() {
    }

    public void onSwapTo() {
    }

    public boolean update() {
        return false;
    }
}
