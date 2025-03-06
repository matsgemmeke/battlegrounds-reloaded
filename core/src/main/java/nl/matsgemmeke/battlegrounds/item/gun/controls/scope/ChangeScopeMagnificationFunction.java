package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

public class ChangeScopeMagnificationFunction implements ItemFunction<GunHolder> {

    @NotNull
    private final Gun gun;

    public ChangeScopeMagnificationFunction(@NotNull Gun gun) {
        this.gun = gun;
    }

    public boolean isAvailable() {
        return gun.isUsingScope();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return false;
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.changeScopeMagnification();
        return true;
    }
}
