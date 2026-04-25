package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.GunRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.gun.Gun;
import nl.matsgemmeke.battlegrounds.item.gun.GunUser;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class GunActionHandler implements ItemActionHandler<Gun> {

    private final GunRegistry gunRegistry;
    private final ItemControllerRegistry itemControllerRegistry;

    @Inject
    public GunActionHandler(GunRegistry gunRegistry, ItemControllerRegistry itemControllerRegistry) {
        this.gunRegistry = gunRegistry;
        this.itemControllerRegistry = itemControllerRegistry;
    }

    @Override
    public Optional<Gun> resolve(GamePlayer gamePlayer, ItemStack itemStack) {
        return gunRegistry.getAssignedGun(gamePlayer, itemStack);
    }

    @Override
    public DispatchResult dispatch(Gun gun, GamePlayer gamePlayer, Action action) {
        ItemController<GunUser> controller = itemControllerRegistry.getGunController(gun.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);
        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
