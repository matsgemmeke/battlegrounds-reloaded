package nl.matsgemmeke.battlegrounds.item.gun.controls.scope;

import nl.matsgemmeke.battlegrounds.item.controls.ItemFunction;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunHolder;
import org.jetbrains.annotations.NotNull;

public class UseScopeFunction implements ItemFunction<GunHolder> {

    @NotNull
    private final Gun gun;

    public UseScopeFunction(@NotNull Gun gun) {
        this.gun = gun;
    }

    public boolean isAvailable() {
        return !gun.isUsingScope();
    }

    public boolean isBlocking() {
        return false;
    }

    public boolean isPerforming() {
        return false;
    }

    public boolean cancel() {
        return gun.cancelScope();
    }

    public boolean perform(@NotNull GunHolder holder) {
        if (!this.isAvailable()) {
            return false;
        }

        gun.applyScope(holder);
        return true;
    }
}
