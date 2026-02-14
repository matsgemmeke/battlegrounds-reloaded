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

        itemRepresentation.setPlaceholder(Placeholder.LOADED_AMOUNT, String.valueOf(updatedLoadedAmount));
        itemRepresentation.setPlaceholder(Placeholder.RESERVE_AMOUNT, String.valueOf(resourceContainer.getReserveAmount()));

        if (updatedLoadedAmount > 0) {
            itemRepresentation.setAmount(updatedLoadedAmount);
        }

        ItemStack itemStack = itemRepresentation.update();
        performer.setHeldItem(itemStack);
    }
}
