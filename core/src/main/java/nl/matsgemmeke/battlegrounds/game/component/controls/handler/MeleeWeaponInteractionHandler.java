package nl.matsgemmeke.battlegrounds.game.component.controls.handler;

import com.google.inject.Inject;
import nl.matsgemmeke.battlegrounds.entity.GamePlayer;
import nl.matsgemmeke.battlegrounds.game.component.controls.DispatchResult;
import nl.matsgemmeke.battlegrounds.game.component.controls.ItemControllerRegistry;
import nl.matsgemmeke.battlegrounds.game.component.item.MeleeWeaponRegistry;
import nl.matsgemmeke.battlegrounds.item.controls.Action;
import nl.matsgemmeke.battlegrounds.item.controls.ActionResult;
import nl.matsgemmeke.battlegrounds.item.controls.ItemController;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeapon;
import nl.matsgemmeke.battlegrounds.item.melee.MeleeWeaponUser;
import org.bukkit.inventory.ItemStack;

public class MeleeWeaponInteractionHandler implements ItemInteractionHandler {

    private final ItemControllerRegistry itemControllerRegistry;
    private final MeleeWeaponRegistry meleeWeaponRegistry;

    @Inject
    public MeleeWeaponInteractionHandler(ItemControllerRegistry itemControllerRegistry, MeleeWeaponRegistry meleeWeaponRegistry) {
        this.itemControllerRegistry = itemControllerRegistry;
        this.meleeWeaponRegistry = meleeWeaponRegistry;
    }

    @Override
    public DispatchResult handleInteraction(GamePlayer gamePlayer, ItemStack itemStack, Action action) {
        MeleeWeapon meleeWeapon = meleeWeaponRegistry.getAssignedMeleeWeapon(gamePlayer, itemStack).orElse(null);

        if (meleeWeapon == null) {
            return DispatchResult.unhandled();
        }

        ItemController<MeleeWeaponUser> controller = itemControllerRegistry.getMeleeWeaponController(meleeWeapon.getId()).orElse(null);

        if (controller == null) {
            return DispatchResult.unhandled();
        }

        ActionResult actionResult = controller.performActionNew(action, gamePlayer);
        return new DispatchResult(actionResult.performed(), actionResult.cancelEvent());
    }
}
