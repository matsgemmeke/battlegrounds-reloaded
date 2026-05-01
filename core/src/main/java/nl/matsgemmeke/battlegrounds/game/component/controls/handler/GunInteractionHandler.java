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

public class GunInteractionHandler implements ItemInteractionHandler {

    private final GunRegistry gunRegistry;
    private final ItemControllerRegistry itemControllerRegistry;

    @Inject
    public GunInteractionHandler(GunRegistry gunRegistry, ItemControllerRegistry itemControllerRegistry) {
        this.gunRegistry = gunRegistry;
        this.itemControllerRegistry = itemControllerRegistry;
    }

    @Override
    public DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        Gun gun = gunRegistry.getAssignedGun(gamePlayer, itemStack).orElse(null);

        if (gun == null) {
            return DispatchResult.unhandled();
        }

        ItemController<GunUser> controller = itemControllerRegistry.getGunController(gun.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);
        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
