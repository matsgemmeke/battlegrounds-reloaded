package nl.matsgemmeke.battlegrounds.item.throwing;

import nl.matsgemmeke.battlegrounds.item.representation.ItemRepresentation;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.LaunchContext;
import nl.matsgemmeke.battlegrounds.item.shoot.launcher.ProjectileLauncher;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public class ThrowHandler {

    private final ItemRepresentation itemRepresentation;
    private final ProjectileLauncher projectileLauncher;
    private int throwsAmount;

    public ThrowHandler(ItemRepresentation itemRepresentation, ProjectileLauncher projectileLauncher, int throwsAmount) {
        this.itemRepresentation = itemRepresentation;
        this.projectileLauncher = projectileLauncher;
        this.throwsAmount = throwsAmount;
    }

    public void performThrow(ThrowPerformer performer) {
        if (throwsAmount <= 0) {
            return;
        }

        throwsAmount --;

        Location direction = performer.getThrowDirection();
        Supplier<Location> soundLocationSupplier = performer::getThrowDirection;
        World world = direction.getWorld();
        LaunchContext launchContext = new LaunchContext(performer, performer, direction, soundLocationSupplier, world);

        projectileLauncher.launch(launchContext);

        ItemStack itemStack = itemRepresentation.update();
        performer.setHeldItem(itemStack);
    }
}
