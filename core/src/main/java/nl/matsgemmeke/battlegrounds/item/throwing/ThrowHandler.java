package nl.matsgemmeke.battlegrounds.item.throwing;

import nl.matsgemmeke.battlegrounds.item.reload.ResourceContainer;
import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.representation.Placeholder;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ThrowHandler {

    private static final int DEFAULT_ITEM_STACK_AMOUNT = 1;

    private final ItemRepresentation itemRepresentation;
    private final ProjectileLauncher projectileLauncher;
    private final ResourceContainer resourceContainer;

    public ThrowHandler(ItemRepresentation itemRepresentation, ProjectileLauncher projectileLauncher, ResourceContainer resourceContainer) {
        this.itemRepresentation = itemRepresentation;
        this.projectileLauncher = projectileLauncher;
        this.resourceContainer = resourceContainer;
    }

    public void performThrow(ThrowPerformer performer) {
        int loadedAmount = resourceContainer.getLoadedAmount();

        if (loadedAmount <= 0) {
            return;
        }

        int updatedLoadedAmount = loadedAmount - 1;
        resourceContainer.setLoadedAmount(updatedLoadedAmount);

        Location direction = performer.getThrowDirection();
        Supplier<Location> soundLocationSupplier = performer::getThrowDirection;
        World world = direction.getWorld();
        LaunchContext launchContext = new LaunchContext(performer, performer, direction, soundLocationSupplier, world);

        projectileLauncher.launch(launchContext);

        int reserveAmount = resourceContainer.getReserveAmount();
        // Set the amount to zero when the melee weapon has neither loaded nor reserve resources left
        int itemStackAmount = Math.min(updatedLoadedAmount + reserveAmount, DEFAULT_ITEM_STACK_AMOUNT);

        itemRepresentation.setAmount(itemStackAmount);
        itemRepresentation.setPlaceholder(Placeholder.LOADED_AMOUNT, String.valueOf(updatedLoadedAmount));
        itemRepresentation.setPlaceholder(Placeholder.RESERVE_AMOUNT, String.valueOf(reserveAmount));

        ItemStack itemStack = itemRepresentation.update();
        performer.setHeldItem(itemStack);
    }
}
